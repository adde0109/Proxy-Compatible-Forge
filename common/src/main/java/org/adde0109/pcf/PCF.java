package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;

import org.adde0109.pcf.common.ModernForwarding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PCF {
    public static final Logger logger = Logger.create("pcf");

    public static ModernForwarding modernForwarding;

    public static final int QUERY_ID = 1203961429;
    public static final String velocityChannel = "velocity:player_info";
    public static Function<String, Object> resourceLocation;

    public static final String directConnErr =
            "Direct connections to this server are not permitted!";
    public static Function<String, Object> component;

    public static Object directConnErrComponent() {
        return component.apply(directConnErr);
    }

    public static Object channelResource() {
        return resourceLocation.apply(velocityChannel);
    }

    public static final List<String> moddedArgumentTypes = new ArrayList<>();
}
