package org.adde0109.pcf.forwarding.network;

import static org.adde0109.pcf.common.FByteBuf.readPayload;
import static org.adde0109.pcf.common.FByteBuf.readUtf;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeResourceLocation;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public record ClientboundCustomQueryPacket(int transactionId, @NotNull CustomQueryPayload payload)
        implements Packet {
    public static ClientboundCustomQueryPacket read(final @NotNull ByteBuf buf) {
        return new ClientboundCustomQueryPacket(
                readVarInt(buf), new CustomQueryPayloadImpl(readUtf(buf), readPayload(buf)));
    }

    @Override
    public void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        writeResourceLocation(buf, this.payload.id());
        this.payload.write(buf);
    }
}
