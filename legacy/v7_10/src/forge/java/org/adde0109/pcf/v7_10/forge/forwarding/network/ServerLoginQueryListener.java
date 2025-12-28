package org.adde0109.pcf.v7_10.forge.forwarding.network;

import net.minecraft.network.INetHandler;

public interface ServerLoginQueryListener extends INetHandler {
    void handleCustomQueryPacket(C2SCustomQueryAnswerPacket packet);
}
