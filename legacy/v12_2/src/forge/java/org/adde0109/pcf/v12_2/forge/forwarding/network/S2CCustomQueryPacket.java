package org.adde0109.pcf.v12_2.forge.forwarding.network;

import static org.adde0109.pcf.common.Identifier.identifier;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;

import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.CustomQueryPayload;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@SuppressWarnings({"RedundantThrows", "unused"})
public final class S2CCustomQueryPacket implements Packet<INetHandlerLoginClient> {
    private ClientboundCustomQueryPacket packet;

    public S2CCustomQueryPacket() {}

    public S2CCustomQueryPacket(final @NotNull ClientboundCustomQueryPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(@NotNull PacketBuffer buf) throws IOException {
        this.packet = ClientboundCustomQueryPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(@NotNull PacketBuffer buf) throws IOException {
        ClientboundCustomQueryPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(@NotNull INetHandlerLoginClient handler) {
        ((ClientLoginQueryListener) handler).handleCustomQuery(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @NotNull ResourceLocation id() {
        return identifier(this.packet.payload().id());
    }

    public @NotNull CustomQueryPayload payload() {
        return this.packet.payload();
    }
}
