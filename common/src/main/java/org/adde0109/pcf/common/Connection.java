package org.adde0109.pcf.common;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public interface Connection {
    InetSocketAddress remoteAddress();

    void setAddress(SocketAddress address);
}
