package org.adde0109.pcf.forwarding.modern;

import java.net.InetSocketAddress;

public interface ConnectionBridge {
    InetSocketAddress bridge$address();

    void bridge$address(InetSocketAddress address);

    void bridge$send(Object packet);
}
