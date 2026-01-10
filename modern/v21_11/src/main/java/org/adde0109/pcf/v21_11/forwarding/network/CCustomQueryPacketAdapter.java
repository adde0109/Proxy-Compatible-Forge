package org.adde0109.pcf.v21_11.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;

import org.adde0109.pcf.v20_2.neoforge.forwarding.network.MCQueryPayload_RL;

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
                        new CustomQueryPayloadImpl(object.payload().id().toString(), buf.slice())));
    }

    @Override
    public Result<net.minecraft.network.protocol.login.ClientboundCustomQueryPacket> decode(
            final ClientboundCustomQueryPacket object) {
        final CustomQueryPayload payload;
        if (Constraint.lessThan(MinecraftVersions.V21_11).result()) {
            payload =
                    new MCQueryPayload_RL(
                            identifier(object.payload().id()), object.payload().data());
        } else {
            payload =
                    new MCQueryPayload(identifier(object.payload().id()), object.payload().data());
        }
        return Result.success(
                new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                        object.transactionId(), payload));
    }
}
