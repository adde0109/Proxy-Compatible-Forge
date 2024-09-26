package org.adde0109.pcf.mixin.v1_20_2.neoforge.login;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.server.network.ServerLoginPacketListenerImpl", priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    void arclight$preLogin() throws Exception {}
}
