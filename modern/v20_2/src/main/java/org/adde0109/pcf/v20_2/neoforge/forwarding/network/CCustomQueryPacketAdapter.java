package org.adde0109.pcf.v20_2.neoforge.forwarding.network;

import static org.adde0109.pcf.common.Identifier.identifier;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.forwarding.network.protocol.login.ClientboundCustomQueryPacket;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryPayloadImpl;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.jetbrains.annotations.NotNull;

@AConstraint(mappings = Mappings.MOJANG)
public final class CCustomQueryPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ClientboundCustomQueryPacket,
                ClientboundCustomQueryPacket> {
    public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

    @Override
    public @NotNull ClientboundCustomQueryPacket fromMC(
            final @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket
                            mcObject) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        mcObject.payload().write(buf);
        return new ClientboundCustomQueryPacket(
                mcObject.transactionId(),
                new CustomQueryPayloadImpl(mcObject.payload().id().toString(), buf.slice()));
    }

    @Override
    public @NotNull net.minecraft.network.protocol.login.ClientboundCustomQueryPacket toMC(
            final @NotNull ClientboundCustomQueryPacket object) {
        return new net.minecraft.network.protocol.login.ClientboundCustomQueryPacket(
                object.transactionId(),
                new MCQueryPayload(identifier(object.payload().id()), object.payload().data()));
    }
}
