package org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@AConstraint(mappings = Mappings.LEGACY_SEARGE, version = @Versions(min = MinecraftVersion.V14))
@Mixin(Connection.class)
public abstract class ConnectionMixin implements ConnectionBridge {
    // spotless:off
    @Shadow private SocketAddress address;
    @Shadow public abstract void shadow$send(Packet<?> packet);
    // spotless:on

    @Override
    public InetSocketAddress bridge$address() {
        return (InetSocketAddress) this.address;
    }

    @Override
    public void bridge$address(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void bridge$send(Object packet) {
        this.shadow$send((Packet<?>) packet);
    }
}
