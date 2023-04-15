package org.adde0109.pcf.mixin.login;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 250)
public class MethodPlaceholderMixin {
    void arclight$preLogin() throws Exception {}
}
