package org.adde0109.pcf.v20_2.neoforge.forwarding.network;

import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.NotNull;

/** Dummy interface so QueryAnswerPayload can work in Searge-mapped environments. */
public sealed interface QueryAnswerPayloadSearge permits MCQueryAnswerPayload {
    void write(final @NotNull FriendlyByteBuf buf);

    @SuppressWarnings("unused")
    default void m_295630_(final @NotNull FriendlyByteBuf buf) {
        this.write(buf);
    }
}
