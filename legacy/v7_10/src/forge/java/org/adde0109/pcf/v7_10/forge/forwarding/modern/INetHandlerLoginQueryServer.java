package org.adde0109.pcf.v7_10.forge.forwarding.modern;

import net.minecraft.network.INetHandler;

public interface INetHandlerLoginQueryServer extends INetHandler {
    void handleCustomQuery(CCustomQueryPacket packet);
}
