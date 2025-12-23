package org.adde0109.pcf.forwarding.modern;

public interface ServerLoginPacketListenerBridge {
    int pcf$velocityLoginMessageId();

    void pcf$setVelocityLoginMessageId(int id);

    ConnectionBridge pcf$connection();
}
