package org.adde0109.pcf.v20_2.neoforge.forwarding.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.NotNull;

public record MCQueryAnswerPayload(@NotNull ByteBuf data)
        implements net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload,
                QueryAnswerPayloadSearge {
    @Override
    public void write(final @NotNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
