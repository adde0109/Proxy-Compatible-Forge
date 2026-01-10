package org.adde0109.pcf.v20_4.forge.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

public final class CCustomQueryPacketAdapter
        implements ReversibleCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public Result<ClientboundCustomQueryPacket> encode(
            final net.minecraft.network.protocol.login.ClientboundCustomQueryPacket object) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        object.payload().write(buf);
        return Result.success(
                new ClientboundCustomQueryPacket(
                        object.transactionId(),
                        CustomQueryPayload.codec(object.payload().id().toString())
                                .decode(buf.slice())));
    }

    @Override
    public Result<net.minecraft.network.protocol.login.ClientboundCustomQueryPacket> decode(
            final ClientboundCustomQueryPacket object) {
        return Result.success(
                new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                        object.transactionId(),
                        new MCQueryPayload(
                                identifier(object.payload().id()), object.payload().data())));
    }
}
