package org.adde0109.pcf.v20_2.neoforge.forwarding.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;

import org.jspecify.annotations.NonNull;

public final class MCQueryPayload_RL implements CustomQueryPayload {
    private final @NonNull ResourceLocation id;
    private final @NonNull ByteBuf data;

    public MCQueryPayload_RL(@NonNull Object id, @NonNull ByteBuf data) {
        this.id = (ResourceLocation) id;
        this.data = data;
    }

    @Override
    public @NonNull ResourceLocation id() {
        return this.id;
    }

    @Override
    public void write(final @NonNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
