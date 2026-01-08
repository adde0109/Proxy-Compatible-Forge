package org.adde0109.pcf.v12_2.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;

import org.jspecify.annotations.NonNull;

import java.io.IOException;

@SuppressWarnings({"RedundantThrows", "unused"})
public final class S2CCustomQueryPacket implements Packet<INetHandlerLoginClient> {
    private ClientboundCustomQueryPacket packet;

    public S2CCustomQueryPacket() {}

    public S2CCustomQueryPacket(final @NonNull ClientboundCustomQueryPacket packet) {
        this.packet = packet;
    }

    @Override
    public void readPacketData(@NonNull PacketBuffer buf) throws IOException {
        this.packet = ClientboundCustomQueryPacket.STREAM_CODEC.decode(buf);
    }

    @Override
    public void writePacketData(@NonNull PacketBuffer buf) throws IOException {
        ClientboundCustomQueryPacket.STREAM_CODEC.encode(buf, this.packet);
    }

    @Override
    public void processPacket(@NonNull INetHandlerLoginClient handler) {
        ((ClientLoginQueryListener) handler).handleCustomQuery(this);
    }

    public int transactionId() {
        return this.packet.transactionId();
    }

    public @NonNull ResourceLocation id() {
        return identifier(this.packet.payload().id());
    }

    public @NonNull CustomQueryPayload payload() {
        return this.packet.payload();
    }
}
