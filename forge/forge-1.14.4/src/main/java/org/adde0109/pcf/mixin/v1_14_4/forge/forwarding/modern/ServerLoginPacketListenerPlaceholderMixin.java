package org.adde0109.pcf.mixin.v1_14_4.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"unused", "RedundantThrows"})
@ReqMCVersion(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5)
@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    @Unique
    void arclight$preLogin() throws Exception {}
}
