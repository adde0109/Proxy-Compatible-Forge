package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerMixin_Impl
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final Connection connection;
    @Unique private int pcf$velocityLoginMessageId = -1;
    // spotless:on

    @Override
    public int pcf$velocityLoginMessageId() {
        return this.pcf$velocityLoginMessageId;
    }

    @Override
    public void pcf$setVelocityLoginMessageId(int id) {
        this.pcf$velocityLoginMessageId = id;
    }

    @Override
    public ConnectionBridge pcf$connection() {
        return (ConnectionBridge) this.connection;
    }
}
