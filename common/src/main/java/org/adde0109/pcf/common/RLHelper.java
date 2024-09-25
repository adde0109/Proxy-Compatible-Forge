package org.adde0109.pcf.common;

import java.util.function.Function;

public class RLHelper {
    public static final String velocityChannel = "velocity:player_info";
    public static Function<String, Object> resourceLocation = null;

    public static Object get() {
        return resourceLocation.apply(velocityChannel);
    }
}
