package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

import org.adde0109.pcf.v12_2.forge.forwarding.modern.CCustomQueryPacket;
import org.adde0109.pcf.v12_2.forge.forwarding.modern.SCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V8, max = MinecraftVersion.V12_2))
@Mixin(EnumConnectionState.class)
public abstract class EnumConnectionStateMixin {
    // spotless:off
    @Shadow protected abstract EnumConnectionState shadow$registerPacket(EnumPacketDirection direction, Class<? extends Packet<?>> packetClass);
    // spotless:on

    @Unique private Class<? extends Packet<?>> pcf$SCheckClass = null;
    @Unique private Class<? extends Packet<?>> pcf$CCheckClass = null;

    @SuppressWarnings("unchecked")
    @Inject(method = "registerPacket", at = @At(value = "RETURN"))
    private void onRegister(
            EnumPacketDirection direction,
            Class<? extends Packet<?>> packetClass,
            CallbackInfoReturnable<EnumConnectionState> cir) {
        try {
            if (this.pcf$SCheckClass == null || this.pcf$CCheckClass == null) {
                if (Constraint.builder().min(MinecraftVersions.V9).build().result()) {
                    this.pcf$SCheckClass =
                            (Class<? extends Packet<?>>)
                                    Class.forName(
                                                    "net.minecraft.network.login.server.SPacketEnableCompression")
                                            .asSubclass(Packet.class);
                    this.pcf$CCheckClass =
                            (Class<? extends Packet<?>>)
                                    Class.forName(
                                                    "net.minecraft.network.login.client.CPacketEncryptionResponse")
                                            .asSubclass(Packet.class);
                } else {
                    this.pcf$SCheckClass =
                            (Class<? extends Packet<?>>)
                                    Class.forName(
                                                    "net.minecraft.network.login.server.S03PacketEnableCompression")
                                            .asSubclass(Packet.class);
                    this.pcf$CCheckClass =
                            (Class<? extends Packet<?>>)
                                    Class.forName(
                                                    "net.minecraft.network.login.client.C01PacketEncryptionResponse")
                                            .asSubclass(Packet.class);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (packetClass == this.pcf$SCheckClass) {
            this.shadow$registerPacket(EnumPacketDirection.CLIENTBOUND, SCustomQueryPacket.class);
        } else if (packetClass == this.pcf$CCheckClass) {
            this.shadow$registerPacket(EnumPacketDirection.SERVERBOUND, CCustomQueryPacket.class);
        }
    }
}
