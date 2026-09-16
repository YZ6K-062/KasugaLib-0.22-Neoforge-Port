package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.network.Packet;
import kasuga.lib.core.network.S2CPacket;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Channel is used for network packages. If you have some custom data need to be transmitted between
 * Logical Client and Logical Server, for more info about Logical Side, see {@link net.neoforged.fml.LogicalSide}
 * After registration, You could use this channel to transmit your packets.
 * For packets from client to server, see {@link C2SPacket}.
 * For packets from server to client, see {@link S2CPacket}
 */
public class ChannelReg extends Reg {
    private PayloadRegistrar registrar = null;
    private String brand = registrationKey;
    private int id = 0;
    private String namespace = "kasuga_lib";
    private final LinkedList<PacketBuilder<? extends Packet>> packetBuilders;
    /**
     * payload class -> the {@link CustomPacketPayload.Type} it was registered with.
     * <p>
     * {@link Packet#type()} simply returns a field that only the codec's *decoder* fills in
     * ({@link Packet#setType}). A packet that is constructed locally and handed to
     * {@code PacketDistributor} therefore reported {@code type() == null}, and NeoForge's
     * {@code NetworkRegistry#checkPacket} blew up with
     * <pre>
     * NullPointerException: Cannot invoke "CustomPacketPayload$Type.id()" because the return
     * value of "CustomPacketPayload.type()" is null
     * </pre>
     * Thrown from {@code PlayerList#placeNewPlayer}, that surfaced to the client as
     * <em>"Invalid player data"</em> and made the world unjoinable. {@link #stamp} fixes it by
     * tagging outbound packets with the type known at registration time.
     */
    private final Map<Class<?>, CustomPacketPayload.Type<?>> packetTypes = new HashMap<>();
    Predicate<String> clientVersions = ((input) -> true), serverVersions = ((input) -> true);

    /**
     * Create a registry.
     * @param registrationKey name of your channel reg.
     */
    public ChannelReg(String registrationKey) {
        super(registrationKey);
        this.packetBuilders = new LinkedList<>();
    }

    /**
     * Your channel's version.
     * @param brand version.
     * @return self.
     */
    @Mandatory
    public ChannelReg brand(String brand) {
        this.brand = brand;
        return this;
    }

    /**
     * Pass a predicate method here. Different player may use different version of your mod in multiplayer.
     * The game would use this function to examine different versions of your packet.
     * If this method return false, the game will reject the packet.
     * @param clientVersions the predicate method. Usually a lambda, just return True in default.
     * @return self.
     */
    @Optional
    public ChannelReg clientVersions(@NotNull Predicate<String> clientVersions) {
        this.clientVersions = Objects.requireNonNull(clientVersions);
        return this;
    }

    /**
     * Pass a predicate method here. Different player may use different version of your mod in multiplayer.
     * The game would use this function to examine different versions of your packet.
     * If this method return false, the game will reject the packet.
     * @param serverVersions the predicate method. Usually a lambda, just return True in default.
     * @return self.
     */
    @Optional
    public ChannelReg serverVersions(@NotNull Predicate<String> serverVersions) {
        this.serverVersions = Objects.requireNonNull(serverVersions);
        return this;
    }

    /**
     * Register a packet into this channel. Client to server packet, see {@link C2SPacket},
     * server to client packet, see {@link S2CPacket}
     * @param packetClass Class of your packet.
     * @param decoder The decoder method of your packet.
     * @return self.
     */
    @Optional
    public ChannelReg loadPacket(Class<?> packetClass, Function<FriendlyByteBuf, ?> decoder) {
        boolean flag0 = C2SPacket.class.isAssignableFrom(packetClass);
        boolean flag1 = S2CPacket.class.isAssignableFrom(packetClass);
        if(!flag0 && !flag1) return this;
        loadPacket(Pair.of(packetClass, decoder));
        return this;
    }

    /**
     * Submit your registration to forge and minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public ChannelReg submit(SimpleRegistry registry) {
        this.namespace = registry.namespace;
        registry.eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            registrar = event.registrar(brand);
            if(!packetBuilders.isEmpty()){
                for (PacketBuilder<?> builder : packetBuilders) {
                    builder.build();
                }
                packetBuilders.clear();
            }
        });
        return this;
    }

    /**
     * You could use this method only in the logical client. Use this to send a {@link C2SPacket} to the server.
     * @param msg Your packet.
     */
    public void sendToServer(C2SPacket msg) {
        PacketDistributor.sendToServer(stamp(msg));
    }

    /**
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a client.
     * @param msg your packet.
     * @param connection Connection you use.
     */
    public void sendToClient(S2CPacket msg, Connection connection) {
        connection.send(new ClientboundCustomPayloadPacket(stamp(msg)));
    }

    /**
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to a single player's client.
     * @param msg your packet.
     * @param player player you would send.
     */
    public void sendToClient(S2CPacket msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, stamp(msg));
    }

    /**
     * You could only use this method in the logical server. Send your {@link S2CPacket} from the server
     * to all connected client.
     * @param msg your packet.
     * @param level The server level.
     * @param pos the block pos you sent this packet.
     */
    public void boardcastToClients(S2CPacket msg, ServerLevel level, BlockPos pos) {
        level.getChunkSource().chunkMap.getPlayers(level.getChunk(pos).getPos(), false)
                .forEach(player -> sendToClient(msg, player));
    }

    /**
     * Tag a locally built packet with the {@link CustomPacketPayload.Type} it was registered with.
     * No-op when the type is already set (e.g. packets produced by the codec's decoder).
     */
    @Inner
    @SuppressWarnings("unchecked")
    private <T extends Packet> T stamp(T msg) {
        if (msg == null) return null;
        if (msg.type() == null) {
            CustomPacketPayload.Type<?> type = packetTypes.get(msg.getClass());
            if (type != null) {
                msg.setType((CustomPacketPayload.Type<? extends CustomPacketPayload>) type);
            }
        }
        return msg;
    }

    @Override
    public String getIdentifier() {
        return "channel";
    }

    @Inner
    private <T extends Packet> ChannelReg loadPacket(Pair<Class<?>, Function<FriendlyByteBuf, ?>> pair) {
        if(registrar == null) {
            packetBuilders.add(() -> loadPacket(pair));
        } else {
            Class<T> clazz = (Class<T>) pair.getFirst();
            CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(namespace, registrationKey + "/" + id++));
            // Remember the type so outbound (locally built) packets can be stamped with it too.
            packetTypes.put(clazz, type);
            StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                    (buf, packet) -> packet.encode(buf),
                    buf -> {
                        T packet = (T) pair.getSecond().apply(buf);
                        packet.setType(type);
                        return packet;
                    }
            );
            IPayloadHandler<T> handler = (packet, context) -> packet.onReach(context);
            if(C2SPacket.class.isAssignableFrom(clazz)) {
                registrar.playToServer(type, codec, handler);
            } else {
                registrar.playToClient(type, codec, handler);
            }
        }
        return this;
    }

    interface PacketBuilder<T extends C2SPacket> {
        void build();
    }
}
