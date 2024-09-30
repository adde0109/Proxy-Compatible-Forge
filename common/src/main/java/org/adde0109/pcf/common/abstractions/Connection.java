package org.adde0109.pcf.common.abstractions;

import java.net.SocketAddress;

public interface Connection {
    SocketAddress remoteAddress();

    void setAddress(SocketAddress address);
}
