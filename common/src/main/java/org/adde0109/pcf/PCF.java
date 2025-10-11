package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PCF {
    private PCF() {}

    private static final PCF INSTANCE = new PCF();
    public static final Logger logger = Logger.create("pcf");

    public static PCF instance() {
        return INSTANCE;
    }

    private String forwardingSecret;
    private final List<String> MODDED_ARGUMENT_TYPES = new ArrayList<>();

    public String forwardingSecret() {
        return this.forwardingSecret;
    }

    @ApiStatus.Internal
    public void setForwardingSecret(String secret) {
        this.forwardingSecret = secret;
    }

    public List<String> moddedArgumentTypes() {
        return this.MODDED_ARGUMENT_TYPES;
    }

    @ApiStatus.Internal
    public void addModdedArgumentTypes(Collection<String> types) {
        this.MODDED_ARGUMENT_TYPES.addAll(types);
    }
}
