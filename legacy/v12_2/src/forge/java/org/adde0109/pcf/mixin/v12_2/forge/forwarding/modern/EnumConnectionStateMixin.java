package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.server.SPacketEnableCompression;

import org.adde0109.pcf.v12_2.forge.network.CCustomQueryPacket;
import org.adde0109.pcf.v12_2.forge.network.SCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V12, max = MinecraftVersion.V12_2))
@Mixin(EnumConnectionState.class)
public abstract class EnumConnectionStateMixin {
    // spotless:off
    @Shadow protected abstract EnumConnectionState shadow$registerPacket(EnumPacketDirection enumPacketDirection, Class<? extends Packet<?>> packetClass);
    // spotless:on

    @Inject(method = "registerPacket", at = @At(value = "RETURN"))
    private void onRegister(
            EnumPacketDirection direction,
            Class<? extends Packet<?>> packetClass,
            CallbackInfoReturnable<EnumConnectionState> cir) {
        if (packetClass == SPacketEnableCompression.class) {
            this.shadow$registerPacket(EnumPacketDirection.CLIENTBOUND, CCustomQueryPacket.class);
        } else if (packetClass == CPacketEncryptionResponse.class) {
            this.shadow$registerPacket(EnumPacketDirection.SERVERBOUND, SCustomQueryPacket.class);
        }
    }

    // TODO: See if this is necessary
    @Inject(method = "getPacketId", at = @At("HEAD"), cancellable = true)
    private void onGetPacketId(
            EnumPacketDirection direction,
            Packet<?> packetIn,
            CallbackInfoReturnable<Integer> cir) {
        if (packetIn instanceof CCustomQueryPacket) {
            cir.setReturnValue(0x4);
        } else if (packetIn instanceof SCustomQueryPacket) {
            cir.setReturnValue(0x2);
        }
    }
}
