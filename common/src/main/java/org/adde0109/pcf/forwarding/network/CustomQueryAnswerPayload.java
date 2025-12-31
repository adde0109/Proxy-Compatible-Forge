package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.codec.StreamDecoder;
import org.adde0109.pcf.forwarding.network.codec.StreamMemberEncoder;
import org.jetbrains.annotations.NotNull;

public interface CustomQueryAnswerPayload {
    StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryAnswerPayload> DEFAULT_CODEC =
            CustomQueryAnswerPayloadImpl.STREAM_CODEC;

    @NotNull ByteBuf data();

    default @NotNull StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryAnswerPayload>
            codec() {
        return DEFAULT_CODEC;
    }

    static <B extends @NotNull ByteBuf, T extends @NotNull CustomQueryAnswerPayload>
            StreamCodec<B, T> codec(
                    final @NotNull StreamMemberEncoder<B, T> encoder,
                    final @NotNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
