package org.adde0109.pcf.v12_2.forge.network;

import net.minecraft.network.INetHandler;

public interface INetHandlerLoginQueryClient extends INetHandler {
    void handleCustomQuery(CCustomQueryPacket packet);
}
