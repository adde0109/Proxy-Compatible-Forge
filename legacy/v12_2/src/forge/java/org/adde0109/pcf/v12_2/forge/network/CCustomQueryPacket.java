package org.adde0109.pcf.v12_2.forge.network;

import static org.adde0109.pcf.common.FByteBuf.readNullable;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

import java.io.IOException;

@SuppressWarnings({"RedundantThrows", "unused"})
public final class CCustomQueryPacket implements Packet<INetHandlerLoginServer> {
    private static final int pcf$MAX_PAYLOAD_SIZE = 1048576;

    private int transactionId;
    private ByteBuf data;

    public CCustomQueryPacket() {}

    public CCustomQueryPacket(int transactionId, ByteBuf data) {
        this.transactionId = transactionId;
        this.data = Unpooled.copiedBuffer(data);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.transactionId = readVarInt(buf);
        this.data =
                readNullable(
                        buf,
                        (buf2) -> {
                            int i = buf2.readableBytes();
                            if (i >= 0 && i <= pcf$MAX_PAYLOAD_SIZE) {
                                return buf2.readBytes(i);
                            } else {
                                throw new IllegalArgumentException(
                                        "Payload may not be larger than "
                                                + pcf$MAX_PAYLOAD_SIZE
                                                + " bytes");
                            }
                        });
    }

    public void writePacketData(PacketBuffer buffer) throws IOException {
        writeVarInt(buffer, this.transactionId);
        buffer.writeBytes(this.data.copy());
    }

    public void processPacket(INetHandlerLoginServer handler) {
        ((INetHandlerLoginQueryServer) handler).handleCustomQuery(this);
    }

    public int transactionId() {
        return this.transactionId;
    }

    public ByteBuf data() {
        return this.data;
    }
}
