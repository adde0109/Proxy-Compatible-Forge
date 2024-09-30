package org.adde0109.pcf.mixin.v1_14_4.forge.login;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"unused", "RedundantThrows"})
@ReqMCVersion(min = MinecraftVersion.V1_14, max = MinecraftVersion.V1_16_5)
@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    @Unique
    void arclight$preLogin() throws Exception {}
}
