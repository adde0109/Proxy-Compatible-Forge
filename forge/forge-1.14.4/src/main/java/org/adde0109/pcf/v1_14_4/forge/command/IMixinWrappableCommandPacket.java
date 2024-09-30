package org.adde0109.pcf.v1_14_4.forge.command;

import net.minecraft.network.FriendlyByteBuf;

public interface IMixinWrappableCommandPacket {
    void write(FriendlyByteBuf byteBuf, boolean wrap);
}
