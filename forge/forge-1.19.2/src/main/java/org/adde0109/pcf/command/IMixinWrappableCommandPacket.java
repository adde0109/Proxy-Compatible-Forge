package org.adde0109.pcf.command;

import net.minecraft.network.FriendlyByteBuf;

public interface IMixinWrappableCommandPacket {
  public void wrapAndWrite(FriendlyByteBuf byteBuf);
}
