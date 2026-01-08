package org.adde0109.pcf.forwarding.modern;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readResourceLocation;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writePayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeResourceLocation;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

/**
 * Payload for the player info query <br>
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/network/protocol/login/ClientboundCustomQueryPacket.java.patch#L8-L15">PaperMC</a>
 *
 * @param data the buffer
 */
public record PlayerInfoQueryPayload(@NonNull ByteBuf data) implements CustomQueryPayload {
    public static final StreamCodec<ByteBuf, PlayerInfoQueryPayload> STREAM_CODEC =
            CustomQueryPayload.codec(PlayerInfoQueryPayload::write, PlayerInfoQueryPayload::read);

    private static @NonNull PlayerInfoQueryPayload read(final @NonNull ByteBuf buf) {
        readResourceLocation(buf); // Discard
        return new PlayerInfoQueryPayload(readPayload(buf));
    }

    private void write(final @NonNull ByteBuf buf) {
        writeResourceLocation(buf, this.id());
        writePayload(buf, this.data);
    }

    @Override
    public @NonNull String id() {
        return VelocityProxy.PLAYER_INFO_CHANNEL.toString();
    }

    @Override
    public @NonNull StreamCodec<@NonNull ByteBuf, @NonNull PlayerInfoQueryPayload> codec() {
        return STREAM_CODEC;
    }
}
