package org.adde0109.pcf.v1_20_2.neoforge.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;

import org.jetbrains.annotations.NotNull;

/**
 * <a
 * href="https://github.com/PaperMC/Paper/blob/bd5867a96f792f0eb32c1d249bb4bbc1d8338d14/patches/server/0009-MC-Utils.patch#L6059-L6073">Simplified
 * version of Paper's implementation</a>
 */
// Paper start - MC Utils - default query payloads
public record QueryAnswerPayload(FriendlyByteBuf buffer) implements CustomQueryAnswerPayload {
    @Override
    public void write(@NotNull FriendlyByteBuf buff) {
        buff.writeBytes(buffer().copy());
    }
}
// Paper end - MC Utils - default query payloads
