package org.adde0109.pcf.common;

import java.net.SocketAddress;

public interface Connection {
    SocketAddress remoteAddress();

    void setAddress(SocketAddress address);
}
