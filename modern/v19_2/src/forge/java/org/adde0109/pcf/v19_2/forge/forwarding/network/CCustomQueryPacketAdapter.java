package org.adde0109.pcf.v19_2.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import net.minecraft.network.FriendlyByteBuf;

public final class CCustomQueryPacketAdapter
        implements ReversibleCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public Result<ClientboundCustomQueryPacket> encode(
            final net.minecraft.network.protocol.login.ClientboundCustomQueryPacket object) {
        return Result.success(
                new ClientboundCustomQueryPacket(
                        object.getTransactionId(),
                        new CustomQueryPayloadImpl(
                                object.getIdentifier().toString(), object.getData().slice())));
    }

    @Override
    public Result<net.minecraft.network.protocol.login.ClientboundCustomQueryPacket> decode(
            final ClientboundCustomQueryPacket object) {
        return Result.success(
                new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                        object.transactionId(),
                        identifier(object.payload().id()),
                        new FriendlyByteBuf(object.payload().data().slice())));
    }
}
