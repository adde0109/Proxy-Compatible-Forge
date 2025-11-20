package org.adde0109.pcf.mixin.v1_20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readNullable;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.adde0109.pcf.v1_20_2.neoforge.Compatibility;
import org.adde0109.pcf.v1_20_2.neoforge.forwarding.modern.QueryAnswerPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
    @Unique private static final int pcf$MAX_PAYLOAD_SIZE = 1048576;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = {"readPayload", "m_293399_"},
            at = @At("HEAD"),
            cancellable = true)
    private static void onReadPayload(
            int queryId, @Coerce ByteBuf buf, CallbackInfoReturnable<Object> cir) {
        if (QUERY_IDS.contains(queryId)) {
            QUERY_IDS.remove(queryId);
            // spotless:off
            ByteBuf buffer = readNullable(buf, (buf2) -> {
                int i = buf2.readableBytes();
                if (i >= 0 && i <= pcf$MAX_PAYLOAD_SIZE) {
                    return buf2.readBytes(i);
                } else {
                    throw new IllegalArgumentException("Payload may not be larger than " + pcf$MAX_PAYLOAD_SIZE + " bytes");
                }
            });
            cir.setReturnValue(buffer == null ? null : new QueryAnswerPayload(buffer));
            // spotless:on
            Compatibility.neoForgeReturnSimpleQueryPayload(buffer, queryId, cir);
            cir.cancel();
        }
    }
}
