package org.adde0109.pcf.v21_11.forwarding.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayloadImpl;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;

import org.adde0109.pcf.v20_2.neoforge.forwarding.network.MCQueryPayload_RL;
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
        final CustomQueryPayload payload;
        if (Constraint.lessThan(MinecraftVersions.V21_11).result()) {
            payload =
                    new MCQueryPayload_RL(
                            identifier(object.payload().id()), object.payload().data());
        } else {
            payload =
                    new MCQueryPayload(identifier(object.payload().id()), object.payload().data());
        }
        return new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                object.transactionId(), payload);
    }
}
