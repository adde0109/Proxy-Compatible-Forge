package org.adde0109.pcf.mixin.v1_20_4.forge.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readNullable;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.adde0109.pcf.v1_20_4.forge.forwarding.modern.QueryAnswerPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <a
 * href="https://github.com/PaperMC/Paper/blob/bd5867a96f792f0eb32c1d249bb4bbc1d8338d14/patches/server/0009-MC-Utils.patch#L6040-L6050">Adapted
 * from Paper</a>
 */
// TODO: Merge this with MOJANG mapped mixin
@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4)
@Mixin(ServerboundCustomQueryAnswerPacket.class)
public class ServerboundCustomQueryAnswerPacketMixin {
    @Shadow @Final private static int MAX_PAYLOAD_SIZE;

    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void onReadPayload(
            int queryId, @Coerce ByteBuf buf, CallbackInfoReturnable<Object> cir) {
        if (QUERY_IDS.contains(queryId)) {
            QUERY_IDS.remove(queryId);
            // spotless:off
            ByteBuf buffer = readNullable(buf, (buf2) -> {
                int i = buf2.readableBytes();
                if (i >= 0 && i <= MAX_PAYLOAD_SIZE) {
                    return buf2.readBytes(i);
                } else {
                    throw new IllegalArgumentException("Payload may not be larger than " + MAX_PAYLOAD_SIZE + " bytes");
                }
            });
            cir.setReturnValue(buffer == null ? null : new QueryAnswerPayload(buffer));
            // spotless:on
            cir.cancel();
        }
    }
}
