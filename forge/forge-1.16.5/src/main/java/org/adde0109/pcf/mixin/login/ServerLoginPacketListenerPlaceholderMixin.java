package org.adde0109.pcf.mixin.login;

import net.minecraft.network.login.ServerLoginNetHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerLoginNetHandler.class, priority = 250)
public class ServerLoginPacketListenerPlaceholderMixin {
    private void arclight$preLogin() throws Exception {}
}
