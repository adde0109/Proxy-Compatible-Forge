package org.adde0109.pcf.v1_17_1.forge.command;

import net.minecraft.network.FriendlyByteBuf;

public interface IMixinWrappableCommandPacket {
    void write(FriendlyByteBuf byteBuf, boolean wrap);
}
