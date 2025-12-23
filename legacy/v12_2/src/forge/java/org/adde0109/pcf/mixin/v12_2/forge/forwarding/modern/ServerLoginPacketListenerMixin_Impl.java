package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.NetHandlerLoginServer;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V12_2))
@Mixin(NetHandlerLoginServer.class)
public abstract class ServerLoginPacketListenerMixin_Impl
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final public NetworkManager networkManager;
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
        return (ConnectionBridge) this.networkManager;
    }
}
