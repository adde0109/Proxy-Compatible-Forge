package org.adde0109.pcf.crossstitch.compat;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import java.util.List;

public final class ArgumentEdgeCases {
    private ArgumentEdgeCases() {}

    public static boolean isArgumentEdgeCase(String identifier) {
        return isBookshelfEdgeCase(identifier);
    }

    private static final List<String> BOOKSHELF_EDGE_CASES =
            List.of("minecraft:enum", "minecraft:hand", "minecraft:loot", "minecraft:mod");

    /**
     * Bookshelf registers multiple argument ids under the vanilla namespace <br>
     * "minecraft:mod" -> 1.14.x <br>
     * "minecraft:enum" -> 1.14 - 1.15.2, 1.17.1 <br>
     * "minecraft:hand" -> 1.16.x - 1.16.5 <br>
     * "minecraft:loot" -> 1.16.x - 1.17.1
     *
     * @param identifier the argument type resource location
     * @return true if the identifier matches and Bookshelf is loaded
     */
    private static boolean isBookshelfEdgeCase(String identifier) {
        return Constraint.range(MinecraftVersions.V14, MinecraftVersions.V17_1)
                        .deps("bookshelf")
                        .result()
                && BOOKSHELF_EDGE_CASES.contains(identifier);
    }
}
