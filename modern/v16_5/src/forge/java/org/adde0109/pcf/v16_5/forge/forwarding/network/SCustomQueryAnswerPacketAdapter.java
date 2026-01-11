package org.adde0109.pcf.v16_5.forge.forwarding.network;

import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ServerboundCustomQueryPacketAccessor;

public final class SCustomQueryAnswerPacketAdapter
        implements ReversibleCodec<
                ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Result<ServerboundCustomQueryAnswerPacket> encode(
            final ServerboundCustomQueryPacket object) {
        final int transactionId =
                ((ServerboundCustomQueryPacketAccessor) object).pcf$getTransactionId();
        if (((ServerboundCustomQueryPacketAccessor) object).pcf$getData() == null) {
            return Result.success(new ServerboundCustomQueryAnswerPacket(transactionId));
        }
        return Result.success(
                new ServerboundCustomQueryAnswerPacket(
                        transactionId,
                        CustomQueryAnswerPayload.codec(transactionId)
                                .decode(
                                        ((ServerboundCustomQueryPacketAccessor) object)
                                                .pcf$getData()
                                                .slice())));
    }

    @Override
    public Result<ServerboundCustomQueryPacket> decode(
            final ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return Result.success(new ServerboundCustomQueryPacket(object.transactionId(), null));
        }
        return Result.success(
                new ServerboundCustomQueryPacket(
                        object.transactionId(),
                        new FriendlyByteBuf(object.payload().data().slice())));
    }
}
