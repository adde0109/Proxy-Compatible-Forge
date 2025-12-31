package org.adde0109.pcf.forwarding.network.codec;

import org.jetbrains.annotations.NotNull;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> of(StreamEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NotNull V decode(final @NotNull B object) {
                return decoder.decode(object);
            }

            @Override
            public void encode(final @NotNull B object, final @NotNull V value) {
                encoder.encode(object, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(
            StreamMemberEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NotNull V decode(final @NotNull B buffer) {
                return decoder.decode(buffer);
            }

            @Override
            public void encode(final @NotNull B buffer, final @NotNull V value) {
                encoder.encode(value, buffer);
            }
        };
    }
}
