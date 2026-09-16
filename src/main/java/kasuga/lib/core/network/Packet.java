package kasuga.lib.core.network;

import kasuga.lib.core.annos.Inner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is designed for network packages. Network packages transmit our custom data between client and server.
 * For packages from client to server, use {@link C2SPacket}.
 * For packages from server to client, use {@link S2CPacket}
 */
public abstract class Packet implements CustomPacketPayload {

    private Type<? extends CustomPacketPayload> type;

    /**
     * This constructor is also used as decoder. While the program get data from network, it would
     * use this deserializer to create our packet.
     * @param buf the bytes we got from network.
     */
    public Packet(FriendlyByteBuf buf) {}

    public Packet() {}

    @Inner
    public void setType(Type<? extends CustomPacketPayload> type) {
        this.type = type;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }

    @Inner
    public abstract boolean onReach(IPayloadContext context);

    /**
     * The encoder of your packet, you must push all your data into this byte buffer.
     * @param buf the data container buffer, push your data into it.
     */
    public abstract void encode(FriendlyByteBuf buf);
}
