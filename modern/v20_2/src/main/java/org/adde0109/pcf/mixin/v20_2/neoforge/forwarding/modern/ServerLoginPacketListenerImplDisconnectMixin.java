package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.MOJANG,
        version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_6))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplDisconnectMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow public abstract void shadow$onDisconnect(Component reason);
    // spotless:on

    @Override
    public void bridge$disconnect(final @NotNull Object reason) {
        this.shadow$onDisconnect((Component) reason);
    }
}
