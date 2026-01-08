package org.adde0109.pcf.v21_10.forwarding.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;

import org.jspecify.annotations.NonNull;

public record MCQueryAnswerPayload(@NonNull ByteBuf data)
        implements net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload,
                QueryAnswerPayloadSearge {
    @Override
    public void write(final @NonNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
