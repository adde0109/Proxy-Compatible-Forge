package org.adde0109.pcf.v12_2.forge.forwarding.modern;

import static org.adde0109.pcf.common.FByteBuf.readUtf;
import static org.adde0109.pcf.common.FByteBuf.readVarInt;
import static org.adde0109.pcf.common.FByteBuf.writeResourceLocation;
import static org.adde0109.pcf.common.FByteBuf.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

@SuppressWarnings({"RedundantThrows", "unused"})
public final class SCustomQueryPacket implements Packet<INetHandlerLoginClient> {
    private static final int pcf$MAX_PAYLOAD_SIZE = 1048576;

    private int transactionId;
    private ResourceLocation identifier;
    private ByteBuf data;

    public SCustomQueryPacket() {}

    public SCustomQueryPacket(int transactionId, ResourceLocation identifier, ByteBuf data) {
        this.transactionId = transactionId;
        this.identifier = identifier;
        this.data = Unpooled.copiedBuffer(data);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.transactionId = readVarInt(buf);
        this.identifier = new ResourceLocation(readUtf(buf));
        int i = buf.readableBytes();
        if (i >= 0 && i <= pcf$MAX_PAYLOAD_SIZE) {
            this.data = buf.readBytes(i);
        } else {
            throw new IllegalArgumentException(
                    "Payload may not be larger than " + pcf$MAX_PAYLOAD_SIZE + " bytes");
        }
    }

    @Override
    public void writePacketData(PacketBuffer buffer) throws IOException {
        writeVarInt(buffer, this.transactionId);
        writeResourceLocation(buffer, this.identifier);
        buffer.writeBytes(this.data.copy());
    }

    @Override
    public void processPacket(INetHandlerLoginClient handler) {}
}
