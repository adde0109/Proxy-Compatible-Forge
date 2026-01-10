package org.adde0109.pcf.v16_5.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ClientboundCustomQueryPacketAccessor;

public final class CCustomQueryPacketAdapter
        implements ReversibleCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public Result<ClientboundCustomQueryPacket> encode(
            net.minecraft.network.protocol.login.ClientboundCustomQueryPacket object) {
        return Result.success(
                new ClientboundCustomQueryPacket(
                        ((ClientboundCustomQueryPacketAccessor) object).pcf$getTransactionId(),
                        CustomQueryPayload.codec(
                                        ((ClientboundCustomQueryPacketAccessor) object)
                                                .pcf$getIdentifier()
                                                .toString())
                                .decode(
                                        ((ClientboundCustomQueryPacketAccessor) object)
                                                .pcf$getData()
                                                .slice())));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Result<net.minecraft.network.protocol.login.ClientboundCustomQueryPacket> decode(
            ClientboundCustomQueryPacket object) {
        net.minecraft.network.protocol.login.ClientboundCustomQueryPacket mcObject =
                new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket();
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setTransactionId(object.transactionId());
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setIdentifier(identifier(object.payload().id()));
        ((ClientboundCustomQueryPacketAccessor) mcObject)
                .pcf$setData(new FriendlyByteBuf(object.payload().data().slice()));
        return Result.success(mcObject);
    }
}
