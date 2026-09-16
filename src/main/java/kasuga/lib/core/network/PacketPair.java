package kasuga.lib.core.network;

import net.minecraft.network.FriendlyByteBuf;



public abstract class PacketPair {

    public PacketPair() {}
    public abstract void encode(FriendlyByteBuf buf);
    public abstract void decode(FriendlyByteBuf buf);
}
