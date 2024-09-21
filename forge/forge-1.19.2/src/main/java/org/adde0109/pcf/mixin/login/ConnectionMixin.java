package org.adde0109.pcf.mixin.login;

import net.minecraft.network.Connection;
import org.adde0109.pcf.login.IMixinConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;

@Mixin(Connection.class)
public class ConnectionMixin implements IMixinConnection {
    @Shadow
    private SocketAddress address;

    @Override
    public void pcf$setAddress(SocketAddress address) {
        this.address = address;
    }
}