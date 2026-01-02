package org.adde0109.pcf.v16_5.forge.forwarding.network;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayloadImpl;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;

import org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern.ServerboundCustomQueryPacketAccessor;
import org.jetbrains.annotations.NotNull;

public final class SCustomQueryAnswerPacketAdapter
        implements AdapterCodec<ServerboundCustomQueryPacket, ServerboundCustomQueryAnswerPacket> {
    public static final SCustomQueryAnswerPacketAdapter INSTANCE =
            new SCustomQueryAnswerPacketAdapter();

    @Override
    public @NotNull ServerboundCustomQueryAnswerPacket from(
            final @NotNull ServerboundCustomQueryPacket object) {
        if (((ServerboundCustomQueryPacketAccessor) object).pcf$getData() == null) {
            return new ServerboundCustomQueryAnswerPacket(
                    ((ServerboundCustomQueryPacketAccessor) object).pcf$getTransactionId());
        }
        return new ServerboundCustomQueryAnswerPacket(
                ((ServerboundCustomQueryPacketAccessor) object).pcf$getTransactionId(),
                new CustomQueryAnswerPayloadImpl(
                        ((ServerboundCustomQueryPacketAccessor) object).pcf$getData().slice()));
    }

    @Override
    public @NotNull ServerboundCustomQueryPacket to(
            final @NotNull ServerboundCustomQueryAnswerPacket object) {
        if (object.payload() == null) {
            return new ServerboundCustomQueryPacket(object.transactionId(), null);
        }
        return new ServerboundCustomQueryPacket(
                object.transactionId(), new FriendlyByteBuf(object.payload().data().slice()));
    }
}
