package org.adde0109.pcf.v7_10.forge.forwarding.modern;

import net.minecraft.network.INetHandler;

public interface INetHandlerLoginQueryClient extends INetHandler {
    void handleCustomQuery(SCustomQueryPacket packet);
}
