package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.common.FriendlyByteBuf.readNullablePayload;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.TRANSACTION_IDS;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;

import org.adde0109.pcf.v20_2.neoforge.Compatibility;
import org.adde0109.pcf.v20_2.neoforge.forwarding.network.MCQueryAnswerPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <a
 * href="https://github.com/PaperMC/Paper/blob/bd5867a96f792f0eb32c1d249bb4bbc1d8338d14/patches/server/0009-MC-Utils.patch#L6040-L6050">Adapted
 * from Paper</a>
 */
@AConstraint(version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerboundCustomQueryAnswerPacket.class)
public class ServerboundCustomQueryAnswerPacketMixin {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = {"readPayload", "m_293399_"},
            require = 0,
            at = @At("HEAD"),
            cancellable = true)
    private static void onReadPayload(
            final int transactionId,
            final @Coerce @NotNull ByteBuf buf,
            final @NotNull CallbackInfoReturnable<CustomQueryAnswerPayload> cir) {
        if (TRANSACTION_IDS.contains(transactionId)) {
            final @Nullable ByteBuf buffer = readNullablePayload(buf);
            cir.setReturnValue(buffer == null ? null : new MCQueryAnswerPayload(buffer));
            Compatibility.neoForgeReturnSimpleQueryPayload(buffer, transactionId, cir);
            cir.cancel();
        }
    }
}
