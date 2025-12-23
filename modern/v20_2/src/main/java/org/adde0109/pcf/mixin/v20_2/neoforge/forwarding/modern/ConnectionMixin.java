package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(mappings = Mappings.MOJANG)
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
