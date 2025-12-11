package org.adde0109.pcf.v12_2.forge.network;

import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

import java.io.IOException;

public final class SCustomQueryPacket implements Packet<INetHandlerLoginServer> {
    private int transactionId;
    private ByteBuf data;

    public SCustomQueryPacket() {}

    public SCustomQueryPacket(int transactionId, ByteBuf data) {
        this.transactionId = transactionId;
        this.data = new PacketBuffer(data);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.transactionId = readVarInt(buf);
        if (buf.readBoolean()) {
            int i = buf.readableBytes();
            if (i < 0 || i > 1048576) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.data = new PacketBuffer(buf.readBytes(i));
        } else {
            this.data = null;
        }
    }

    public void writePacketData(PacketBuffer buffer) throws IOException {
        writeVarInt(buffer, this.transactionId);
        buffer.writeBytes(this.data.copy());
    }

    public void processPacket(INetHandlerLoginServer handler) {
        ((INetHandlerLoginQueryServer) handler).handleCustomQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public ByteBuf getData() {
        return this.data;
    }
}
