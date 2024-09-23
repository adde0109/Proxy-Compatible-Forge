package org.adde0109.pcf.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public record ClientBoundCustomQueryImpl(ResourceLocation id, FriendlyByteBuf data) implements CustomQueryPayload {
    @Override
    public void write(@NotNull FriendlyByteBuf buff) {
        buff.writeBytes(data().copy());
    }
}
