package org.adde0109.pcf.v21_10.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

import org.jspecify.annotations.NonNull;

@AConstraint(mappings = Mappings.MOJANG)
public final class CCustomQueryPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public @NonNull ClientboundCustomQueryPacket from(
            final net.minecraft.network.protocol.login.@NonNull ClientboundCustomQueryPacket
                    object) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        object.payload().write(buf);
        return new ClientboundCustomQueryPacket(
                object.transactionId(),
                new CustomQueryPayloadImpl(object.payload().id().toString(), buf.slice()));
    }

    @Override
    public net.minecraft.network.protocol.login.@NonNull ClientboundCustomQueryPacket to(
            final @NonNull ClientboundCustomQueryPacket object) {
        return new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                object.transactionId(),
                new MCQueryPayload(identifier(object.payload().id()), object.payload().data()));
    }
}
