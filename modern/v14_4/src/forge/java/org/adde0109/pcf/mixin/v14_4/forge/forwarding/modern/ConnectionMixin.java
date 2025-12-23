package org.adde0109.pcf.mixin.v14_4.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
@Mixin(Connection.class)
public abstract class ConnectionMixin implements ConnectionBridge {
    // spotless:off
    @Shadow public abstract void shadow$send(Packet<?> packet);
    // spotless:on

    @Override
    public void pcf$send(Object packet) {
        this.shadow$send((Packet<?>) packet);
    }
}
