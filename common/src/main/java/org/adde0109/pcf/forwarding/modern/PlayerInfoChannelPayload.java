package org.adde0109.pcf.forwarding.modern;

import io.netty.buffer.ByteBuf;

import org.adde0109.pcf.forwarding.network.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Payload for the player info channel <br>
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/network/protocol/login/ClientboundCustomQueryPacket.java.patch#L8-L15">PaperMC</a>
 *
 * @param id the resource location id
 * @param data the buffer
 */
public record PlayerInfoChannelPayload(@NotNull String id, @NotNull ByteBuf data)
        implements CustomQueryPayload {
    @Override
    public void write(final @NotNull ByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
