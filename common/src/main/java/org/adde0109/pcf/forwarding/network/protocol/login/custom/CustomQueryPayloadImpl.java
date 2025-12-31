package org.adde0109.pcf.forwarding.network.protocol.login.custom;

import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readPayload;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.readResourceLocation;
import static org.adde0109.pcf.forwarding.network.FriendlyByteBuf.writeResourceLocation;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record CustomQueryPayloadImpl(@NotNull String id, @NotNull ByteBuf data)
        implements CustomQueryPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryPayloadImpl>
            STREAM_CODEC =
                    CustomQueryPayload.codec(
                            CustomQueryPayloadImpl::write, CustomQueryPayloadImpl::read);

    private static @NotNull CustomQueryPayloadImpl read(final @NotNull ByteBuf buf) {
        final String id = readResourceLocation(buf);
        final @NotNull ByteBuf data = readPayload(buf);
        return new CustomQueryPayloadImpl(id, data);
    }

    private void write(final @NotNull ByteBuf buf) {
        writeResourceLocation(buf, this.id());
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
