package org.adde0109.pcf.v12_2.forge.forwarding.modern;

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
public final class SCustomQueryPacket implements Packet<INetHandlerLoginClient> {
    private ClientboundCustomQueryPacket packet;

    public SCustomQueryPacket() {}

    public SCustomQueryPacket(final @NotNull ClientboundCustomQueryPacket packet) {
        this.packet = packet;
    }

    public SCustomQueryPacket(final int transactionId, final @NotNull CustomQueryPayload payload) {
        this.packet = new ClientboundCustomQueryPacket(transactionId, payload);
    }

    @Override
    public void readPacketData(@NotNull PacketBuffer buf) throws IOException {
        this.packet = ClientboundCustomQueryPacket.read(buf);
    }

    @Override
    public void writePacketData(@NotNull PacketBuffer buf) throws IOException {
        this.packet.write(buf);
    }

    @Override
    public void processPacket(@NotNull INetHandlerLoginClient handler) {
        ((INetHandlerLoginQueryClient) handler).handleCustomQuery(this);
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
