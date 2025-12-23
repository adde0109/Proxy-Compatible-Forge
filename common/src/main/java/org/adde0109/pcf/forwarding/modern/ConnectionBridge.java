package org.adde0109.pcf.forwarding.modern;

import java.net.SocketAddress;

public interface ConnectionBridge {
    SocketAddress pcf$address();

    void pcf$address(SocketAddress address);

    void pcf$send(Object packet);
}
