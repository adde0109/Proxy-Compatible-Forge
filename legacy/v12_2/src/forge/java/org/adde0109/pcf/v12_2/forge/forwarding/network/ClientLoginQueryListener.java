package org.adde0109.pcf.v12_2.forge.forwarding.network;

import net.minecraft.network.INetHandler;

public interface ClientLoginQueryListener extends INetHandler {
    void handleCustomQuery(S2CCustomQueryPacket packet);
}
