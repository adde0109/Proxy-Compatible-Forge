package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;

import org.adde0109.pcf.forwarding.Mode;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public final class PCF {
    private PCF() {}

    public static final String MOD_ID = "pcf";

    private static final PCF INSTANCE = new PCF();
    public static final Logger logger = Logger.create(MOD_ID);

    public static PCF instance() {
        return INSTANCE;
    }

    private Forwarding forwarding;
    private CrossStitch crossStitch;

    public Forwarding forwarding() {
        return this.forwarding;
    }

    @ApiStatus.Internal
    public void setForwarding(Forwarding forwarding) {
        this.forwarding = forwarding;
    }

    public CrossStitch crossStitch() {
        return this.crossStitch;
    }

    @ApiStatus.Internal
    public void setCrossStitch(CrossStitch crossStitch) {
        this.crossStitch = crossStitch;
    }

    // TODO: Conditionally disable mixins based on config
    public record Forwarding(boolean enabled, Mode mode, String secret) {}

    public record CrossStitch(boolean enabled, List<String> forceWrappedArguments) {}
}
