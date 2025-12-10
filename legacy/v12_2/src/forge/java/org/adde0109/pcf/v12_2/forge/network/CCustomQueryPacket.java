package org.adde0109.pcf.v12_2.forge.network;

import static org.adde0109.pcf.common.FByteBuf.readUtf;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeResourceLocation;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class CCustomQueryPacket implements Packet<INetHandlerLoginClient> {
    private int transactionId;
    private ResourceLocation identifier;
    private ByteBuf data;

    public CCustomQueryPacket() {}

    public CCustomQueryPacket(int transactionId, ResourceLocation identifier, ByteBuf data) {
        this.transactionId = transactionId;
        this.identifier = identifier;
        this.data = new PacketBuffer(data);
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.transactionId = readVarInt(buf);
        this.identifier = new ResourceLocation(readUtf(buf));
        int i = buf.readableBytes();
        if (i >= 0 && i <= 1048576) {
            this.data = new PacketBuffer(buf.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void writePacketData(PacketBuffer buffer) throws IOException {
        writeVarInt(buffer, this.transactionId);
        writeResourceLocation(buffer, this.identifier);
        buffer.writeBytes(this.data.copy());
    }

    public void processPacket(INetHandlerLoginClient handler) {
        ((INetHandlerLoginQueryClient) handler).handleCustomQuery(this);
    }
}
