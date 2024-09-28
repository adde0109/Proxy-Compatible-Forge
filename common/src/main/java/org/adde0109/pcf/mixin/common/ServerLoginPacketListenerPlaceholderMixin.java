package org.adde0109.pcf.mixin.common;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.MinecraftVersion;

import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("RedundantThrows")
@ReqMCVersion(min = MinecraftVersion.V1_20_2)
@Mixin(targets = "net.minecraft.server.network.ServerLoginPacketListenerImpl", priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    void arclight$preLogin() throws Exception {}
}
