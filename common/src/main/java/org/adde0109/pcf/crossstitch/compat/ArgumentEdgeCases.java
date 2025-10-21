package org.adde0109.pcf.crossstitch.compat;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

public final class ArgumentEdgeCases {
    private ArgumentEdgeCases() {}

    public static boolean isArgumentEdgeCase(String identifier) {
        return isLivingThingsEdgeCase(identifier) || isBookshelfEdgeCase(identifier);
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

    private static final boolean IS_BOOKSHELF_LOADED =
            MetaAPI.instance().isModLoaded("bookshelf")
                    && MetaAPI.instance()
                            .version()
                            .isInRange(MinecraftVersions.V14_4, MinecraftVersions.V16_5);

    /**
     * Bookshelf adds "minecraft:hand" and "minecraft:loot" under the vanilla namespace
     *
     * @param identifier the argument type resource location
     * @return true if the identifier is "minecraft:hand" or "minecraft:loot" and Bookshelf is
     *     loaded
     */
    public static boolean isBookshelfEdgeCase(String identifier) {
        return IS_BOOKSHELF_LOADED
                && (identifier.equals("minecraft:hand") || identifier.equals("minecraft:loot"));
    }
}
