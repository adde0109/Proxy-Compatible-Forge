package org.adde0109.pcf.v12_2.forge.forwarding.modern;

import net.minecraft.network.INetHandler;

public interface INetHandlerLoginQueryServer extends INetHandler {
    void handleCustomQueryPacket(CCustomQueryPacket packet);
}
