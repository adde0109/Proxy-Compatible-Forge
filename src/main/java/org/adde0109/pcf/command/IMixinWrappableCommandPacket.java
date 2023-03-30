package org.adde0109.pcf.command;

import net.minecraft.network.PacketBuffer;

public interface IMixinWrappableCommandPacket {
  public void write(PacketBuffer byteBuf, boolean wrap);
}
