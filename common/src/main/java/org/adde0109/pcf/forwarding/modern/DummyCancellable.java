package org.adde0109.pcf.forwarding.modern;

import dev.neuralnexus.taterapi.event.Cancellable;

public class DummyCancellable implements Cancellable {
    public static final DummyCancellable INSTANCE = new DummyCancellable();

    @Override
    public boolean cancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {}
}
