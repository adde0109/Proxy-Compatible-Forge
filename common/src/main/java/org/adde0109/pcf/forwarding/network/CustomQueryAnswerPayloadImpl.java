package org.adde0109.pcf.forwarding.network;

import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readPayload;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record CustomQueryAnswerPayloadImpl(@NotNull ByteBuf data)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryAnswerPayloadImpl>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            CustomQueryAnswerPayloadImpl::write,
                            CustomQueryAnswerPayloadImpl::read);

    private static @NotNull CustomQueryAnswerPayloadImpl read(final @NotNull ByteBuf buf) {
        return new CustomQueryAnswerPayloadImpl(readPayload(buf));
    }

    private void write(final @NotNull ByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryAnswerPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
