package org.adde0109.pcf.mixin.v17_1.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"unused", "RedundantThrows"})
@AConstraint(notPlatform = Platform.ARCLIGHT, version = @Versions(min = MinecraftVersion.V17))
@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 250)
public class ServerLoginPacketListenerHybridPlaceholderMixin {
    @Unique
    void arclight$preLogin() throws Exception {}
}
