package org.adde0109.pcf.v20_4.forge.forwarding.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterCodec;
import org.adde0109.pcf.forwarding.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.forwarding.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;
import org.adde0109.pcf.v20_2.neoforge.forwarding.network.MCQueryAnswerPayload;
import org.jetbrains.annotations.NotNull;

@AConstraint(mappings = Mappings.SEARGE)
public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<
                net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket,
                ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NotNull ServerboundCustomQueryAnswerPacket fromMC(
            final @NotNull net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket
                            mcObject) {
        if (mcObject.payload() == null) {
            return new ServerboundCustomQueryAnswerPacket(mcObject.transactionId());
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        mcObject.payload().write(buf);
        return new ServerboundCustomQueryAnswerPacket(
                mcObject.transactionId(), new CustomQueryAnswerPayloadImpl(buf.slice()));
    }

    @Override
    public @NotNull net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket toMC(
            final @NotNull ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                    object.transactionId(), null);
        }
        return new net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket(
                object.transactionId(), new MCQueryAnswerPayload(object.payload().data()));
    }
}
