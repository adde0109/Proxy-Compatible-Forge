package org.adde0109.pcf.forwarding.network.protocol.login.custom;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.codec.StreamDecoder;
import org.adde0109.pcf.forwarding.network.codec.StreamMemberEncoder;
import org.jetbrains.annotations.NotNull;

public interface CustomQueryPayload {
    StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryPayload> DEFAULT_CODEC =
            CustomQueryPayloadImpl.STREAM_CODEC;

    @NotNull String id();

    @NotNull ByteBuf data();

    default @NotNull StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryPayload> codec() {
        return DEFAULT_CODEC;
    }

    default <T extends @NotNull CustomQueryPayload> T as(
            final @NotNull StreamDecoder<@NotNull ByteBuf, T> codec) {
        return codec.decode(this.data());
    }

    static <B extends @NotNull ByteBuf, T extends @NotNull CustomQueryPayload>
            StreamCodec<B, T> codec(
                    final @NotNull StreamMemberEncoder<B, T> encoder,
                    final @NotNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
