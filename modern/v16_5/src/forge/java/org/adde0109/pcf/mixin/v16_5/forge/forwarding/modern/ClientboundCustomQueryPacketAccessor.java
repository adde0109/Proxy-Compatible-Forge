package org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@AConstraint(mappings = Mappings.LEGACY_SEARGE, version = @Versions(min = MinecraftVersion.V14))
@Mixin(ClientboundCustomQueryPacket.class)
public interface ClientboundCustomQueryPacketAccessor {
    @Accessor("transactionId")
    int pcf$getTransactionId();

    @Accessor("transactionId")
    void pcf$setTransactionId(final int transactionId);

    @Accessor("identifier")
    @NotNull ResourceLocation pcf$getIdentifier();

    @Accessor("identifier")
    void pcf$setIdentifier(final @NotNull ResourceLocation identifier);

    @Accessor("data")
    @NotNull FriendlyByteBuf pcf$getData();

    @Accessor("data")
    void pcf$setData(final @NotNull FriendlyByteBuf data);
}
