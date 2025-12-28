package org.adde0109.pcf.forwarding.modern;

import java.net.SocketAddress;

public interface ConnectionBridge {
    SocketAddress bridge$address();

    void bridge$address(SocketAddress address);

    void bridge$send(Object packet);
}
