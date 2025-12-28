package org.adde0109.pcf.v12_2.forge.forwarding.network;

import net.minecraft.network.INetHandler;

public interface ServerLoginQueryListener extends INetHandler {
    void handleCustomQueryPacket(C2SCustomQueryAnswerPacket packet);
}
