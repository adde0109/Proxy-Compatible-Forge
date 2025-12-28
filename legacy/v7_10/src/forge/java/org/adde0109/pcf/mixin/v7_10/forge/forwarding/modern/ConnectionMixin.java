package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Mixin(NetworkManager.class)
public abstract class ConnectionMixin implements ConnectionBridge {
    // spotless:off
    @Shadow public abstract void shadow$scheduleOutboundPacket(Packet packet, GenericFutureListener<?>... futureListeners);
    // spotless:on

    @Override
    public void bridge$send(Object packet) {
        this.shadow$scheduleOutboundPacket((Packet) packet);
    }
}
