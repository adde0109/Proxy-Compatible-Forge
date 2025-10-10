package org.adde0109.pcf.crossstitch.compat;

import dev.neuralnexus.taterapi.meta.MetaAPI;

public final class ArgumentEdgeCases {
    private ArgumentEdgeCases() {}

    public static boolean isArgumentEdgeCase(String identifier) {
        return isLivingThingsEdgeCase(identifier);
    }

    private static final boolean IS_LIVINGTHINGS_LOADED =
            MetaAPI.instance().isModLoaded("livingthings");

    /**
     * LivingThings modifies the "minecraft:entity" argument type, making it incompatible with
     * vanilla
     *
     * @param identifier the argument type resource location
     * @return true if the identifier is "minecraft:entity" and LivingThings is loaded
     */
    private static boolean isLivingThingsEdgeCase(String identifier) {
        return IS_LIVINGTHINGS_LOADED && identifier.equals("minecraft:entity");
    }
}
