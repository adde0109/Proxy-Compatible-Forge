package org.adde0109.pcf.mixin.common;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.MinecraftVersion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

// TODO: See if Mixin error handler can be used to suppress ClassNotFoundErrors
@SuppressWarnings({"unused", "RedundantThrows"})
@ReqMCVersion(min = MinecraftVersion.V1_14)
@Mixin(
        targets = {
            "net.minecraft.server.network.ServerLoginPacketListenerImpl",
            "net.minecraft.network.login.ServerLoginNetHandler"
        },
        priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    @Unique
    void arclight$preLogin() throws Exception {}
}
