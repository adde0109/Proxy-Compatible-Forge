package org.adde0109.pcf.command;

import net.minecraft.network.FriendlyByteBuf;

public interface IMixinNodeStub {
  public void wrapAndWrite(FriendlyByteBuf byteBuf);
}
