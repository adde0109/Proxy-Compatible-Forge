package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;

import org.adde0109.pcf.forwarding.Mode;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class PCF {
    private PCF() {}

    public static final String MOD_ID = "pcf";
    public static final String CONFIG_FILE_NAME = "pcf-common.toml";

    private static final PCF INSTANCE = new PCF();
    public static final Logger logger = Logger.create(MOD_ID);

    public static PCF instance() {
        return INSTANCE;
    }

    @ApiStatus.Internal
    public static void forceLoadConfig() {
        try {
            if (MetaAPI.instance().isPlatformPresent(Platforms.FORGE)) {
                Class.forName("org.adde0109.pcf.v1_14_4.forge.Config")
                        .getMethod("reload")
                        .invoke(null);
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.NEOFORGE)) {
                Class.forName("org.adde0109.pcf.v1_20_2.neoforge.Config")
                        .getMethod("reload")
                        .invoke(null);
            }
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            logger.error("Failed to load Config class", e);
        }
    }

    private Forwarding forwarding;

    public Forwarding forwarding() {
        return this.forwarding;
    }

    @ApiStatus.Internal
    public void setForwarding(Forwarding forwarding) {
        this.forwarding = forwarding;
    }

    private CrossStitch crossStitch;

    public CrossStitch crossStitch() {
        return this.crossStitch;
    }

    @ApiStatus.Internal
    public void setCrossStitch(CrossStitch crossStitch) {
        this.crossStitch = crossStitch;
    }

    private Debug debug;

    public Debug debug() {
        return this.debug;
    }

    @ApiStatus.Internal
    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    public record Forwarding(boolean enabled, Mode mode, String secret) {}

    public record CrossStitch(boolean enabled, List<String> forceWrappedArguments) {}

    public record Debug(boolean enabled, List<String> disabledMixins) {}
}
