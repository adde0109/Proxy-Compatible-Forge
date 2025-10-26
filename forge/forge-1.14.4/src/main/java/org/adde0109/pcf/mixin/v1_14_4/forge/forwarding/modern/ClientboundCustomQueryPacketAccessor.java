package org.adde0109.pcf.mixin.v1_14_4.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ReqMappings(Mappings.LEGACY_SEARGE)
@ReqMCVersion(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5)
@Mixin(ClientboundCustomQueryPacket.class)
public interface ClientboundCustomQueryPacketAccessor {
    @Accessor("transactionId")
    void setTransactionId(int transactionId);

    @Accessor("identifier")
    void setIdentifier(ResourceLocation identifier);

    @Accessor("data")
    void setData(FriendlyByteBuf data);
}
