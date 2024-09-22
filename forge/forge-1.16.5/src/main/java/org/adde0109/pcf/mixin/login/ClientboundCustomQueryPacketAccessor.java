package org.adde0109.pcf.mixin.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundCustomQueryPacket.class)
public interface ClientboundCustomQueryPacketAccessor {
    @Accessor("transactionId")
    void setTransactionId(int transactionId);

    @Accessor("identifier")
    void setIdentifier(ResourceLocation identifier);

    @Accessor("data")
    void setData(FriendlyByteBuf data);
}
