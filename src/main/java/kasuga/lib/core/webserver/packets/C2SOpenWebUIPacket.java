package kasuga.lib.core.webserver.packets;

import kasuga.lib.core.network.C2SPacket;
import kasuga.lib.core.webserver.KasugaServerAuthenticator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class C2SOpenWebUIPacket extends C2SPacket {

    String path;


    public C2SOpenWebUIPacket(String path) {
        this.path = path;
    }

    public C2SOpenWebUIPacket(FriendlyByteBuf byteBuf) {
        path = byteBuf.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(path);
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(()->{
            ServerPlayer player = (ServerPlayer) context.player();
            KasugaServerAuthenticator.getURL(player, path, false);
        });
    }

}
