package org.adde0109.pcf.login;

import java.net.SocketAddress;

public interface IMixinConnection {
    void pcf$setAddress(SocketAddress address);
}
