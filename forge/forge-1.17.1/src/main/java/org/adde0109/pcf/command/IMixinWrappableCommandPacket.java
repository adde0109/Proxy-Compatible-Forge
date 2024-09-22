package org.adde0109.pcf.command;

import net.minecraft.network.FriendlyByteBuf;

public interface IMixinWrappableCommandPacket {
  void write(FriendlyByteBuf byteBuf, boolean wrap);
}
