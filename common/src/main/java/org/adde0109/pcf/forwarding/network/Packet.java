package org.adde0109.pcf.forwarding.network;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.adde0109.pcf.forwarding.network.codec.StreamDecoder;
import org.adde0109.pcf.forwarding.network.codec.StreamMemberEncoder;
import org.jetbrains.annotations.NotNull;

public interface Packet {
    // void handle(final @NotNull T handler);

    static <B extends ByteBuf, T extends Packet> StreamCodec<B, T> codec(
            final @NotNull StreamMemberEncoder<B, T> encoder,
            final @NotNull StreamDecoder<B, T> decoder) {
        return new StreamCodec<>() {
            @Override
            public T decode(final @NotNull B buffer) {
                return decoder.decode(buffer);
            }

            @Override
            public void encode(final @NotNull B buffer, final @NotNull T value) {
                encoder.encode(value, buffer);
            }
        };
    }
}
