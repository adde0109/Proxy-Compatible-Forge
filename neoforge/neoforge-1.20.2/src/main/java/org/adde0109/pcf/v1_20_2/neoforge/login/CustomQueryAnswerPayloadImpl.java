package org.adde0109.pcf.v1_20_2.neoforge.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;

import org.jetbrains.annotations.NotNull;

public record CustomQueryAnswerPayloadImpl(FriendlyByteBuf data) implements CustomQueryAnswerPayload {
    @Override
    public void write(@NotNull FriendlyByteBuf buff) {
        buff.writeBytes(data().copy());
    }
}
