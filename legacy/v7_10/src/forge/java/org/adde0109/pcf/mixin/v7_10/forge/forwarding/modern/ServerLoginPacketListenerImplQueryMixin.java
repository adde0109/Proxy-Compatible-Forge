package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleCustomQueryPacket;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.NetHandlerLoginServer;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.ServerLoginQueryListener;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Implements(@Interface(iface = ServerLoginQueryListener.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class ServerLoginPacketListenerImplQueryMixin
        implements ServerLoginPacketListenerBridge {
    public void pcf$handleCustomQueryPacket(final @NonNull C2SCustomQueryAnswerPacket packet) {
        handleCustomQueryPacket(this, packet.transactionId(), packet);
    }
}
