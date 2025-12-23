package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readNullablePayload;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.adde0109.pcf.v20_2.neoforge.Compatibility;
import org.adde0109.pcf.v20_2.neoforge.forwarding.network.MCQueryAnswerPayload;
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
            int queryId, @Coerce ByteBuf buf, CallbackInfoReturnable<Object> cir) {
        if (QUERY_IDS.contains(queryId)) {
            QUERY_IDS.remove(queryId);
            ByteBuf buffer = readNullablePayload(buf);
            cir.setReturnValue(buffer == null ? null : new MCQueryAnswerPayload(buffer));
            Compatibility.neoForgeReturnSimpleQueryPayload(buffer, queryId, cir);
            cir.cancel();
        }
    }
}
