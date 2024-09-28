package org.adde0109.pcf.mixin.v1_20_2.neoforge.network;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;
import dev.neuralnexus.taterapi.Platform;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.common.CommonInitializer;
import org.adde0109.pcf.common.abstractions.Payload;
import org.adde0109.pcf.v1_20_2.neoforge.login.QueryAnswerPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * <a href="https://github.com/PaperMC/Paper/blob/bd5867a96f792f0eb32c1d249bb4bbc1d8338d14/patches/server/0009-MC-Utils.patch#L6040-L6050">Adapted from Paper</a>
 */
@ReqMappings(Mappings.MOJMAP)
@ReqMCVersion(min = MinecraftVersion.V1_20_2)
@Mixin(ServerboundCustomQueryAnswerPacket.class)
public class ServerboundCustomQueryAnswerPacketMixin {
    @Shadow @Final private static int MAX_PAYLOAD_SIZE;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void onReadPayload(int queryId, FriendlyByteBuf buf, CallbackInfoReturnable<CustomQueryAnswerPayload> cir) {
        if (queryId == CommonInitializer.QUERY_ID) {
            // Paper start - MC Utils - default query payloads
            // Note: Added interface cast to make it cross-version compatible
            FriendlyByteBuf buffer = (FriendlyByteBuf) ((Payload) buf).readNullable((buf2) -> {
                int i = buf2.readableBytes();
                if (i >= 0 && i <= MAX_PAYLOAD_SIZE) {
                    return (Payload) new FriendlyByteBuf(buf2.readBytes(i));
                } else {
                    throw new IllegalArgumentException("Payload may not be larger than " + MAX_PAYLOAD_SIZE + " bytes");
                }
            });
            cir.setReturnValue(buffer == null ? null : new QueryAnswerPayload(buffer));
            // Paper end - MC Utils - default query payloads
            // NeoForge 1.20.2 start - Work around NeoForge's SimpleQueryPayload
            if (Platform.get().isNeoForgeBased() && MinecraftVersion.get().is(MinecraftVersion.V1_20_2)) {
                try {
                    Class<?> SimpleQueryPayload = Class.forName("net.neoforged.neoforge.network.custom.payload.SimpleQueryPayload");
                    Constructor<?> constructor = SimpleQueryPayload.getDeclaredConstructor(FriendlyByteBuf.class, int.class, ResourceLocation.class);
                    constructor.setAccessible(true);
                    cir.setReturnValue(buffer == null ? null : (CustomQueryAnswerPayload) constructor.newInstance(
                            buffer, CommonInitializer.QUERY_ID, CommonInitializer.channelResource()));
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            // NeoForge 1.20.2 end - Work around NeoForge's SimpleQueryPayload
            cir.cancel();
        }
    }
}
