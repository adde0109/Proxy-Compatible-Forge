package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S02PacketLoginSuccess;

import org.adde0109.pcf.v7_10.forge.AdapterRegistryInit;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.S2CCustomQueryPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.S2CDummyPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Mixin(EnumConnectionState.class)
public abstract class EnumConnectionStateMixin {
    // spotless:off
    @Shadow protected abstract EnumConnectionState shadow$func_150751_a(int id, Class<? extends Packet> packetClass);
    @Shadow protected abstract EnumConnectionState shadow$func_150756_b(int id, Class<? extends Packet> packetClass);

    @Inject(method = {"func_150751_a"}, at = @At(value = "RETURN"))
    private void onRegisterC2S(int id, Class<? extends Packet> packetClass, CallbackInfoReturnable<EnumConnectionState> cir) {
        if (packetClass == C01PacketEncryptionResponse.class) {
            this.shadow$func_150751_a(0x2, C2SCustomQueryAnswerPacket.class);
            new AdapterRegistryInit(); // TODO: Remove this and figure out why it isn't being loaded
        }
    }

    @Inject(method = {"func_150756_b"}, at = @At(value = "RETURN"))
    private void onRegisterS2C(int id, Class<? extends Packet> packetClass, CallbackInfoReturnable<EnumConnectionState> cir) {
        if (packetClass == S02PacketLoginSuccess.class) {
            this.shadow$func_150756_b(0x3, S2CDummyPacket.class);
            this.shadow$func_150756_b(0x4, S2CCustomQueryPacket.class);
        }
    }
    // spotless:on
}
