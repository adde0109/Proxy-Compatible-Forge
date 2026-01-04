package org.adde0109.pcf.crossstitch.compat;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import org.adde0109.pcf.PCF;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class CrossStitch {
    public static final Object MOD_ARGUMENT_INDICATOR = identifier("crossstitch:mod_argument");
    public static final int MOD_ARGUMENT_INDICATOR_V2 = -256;
    public static final Object EMPTY_IDENTIFIER = identifier("");
    public static final int ZERO_LENGTH = 0;

    private static final Set<String> BUILT_IN_REGISTRY_KEYS = Set.of("minecraft", "brigadier");

    public static boolean shouldWrapArgument(final @NotNull String identifier) {
        final boolean isVanilla = BUILT_IN_REGISTRY_KEYS.stream().anyMatch(identifier::startsWith);
        final boolean forceWrapped =
                PCF.instance().crossStitch().forceWrappedArguments().stream()
                        .anyMatch(identifier::equals);
        final boolean forceWrapVanilla =
                PCF.instance().crossStitch().forceWrapVanillaArguments() && isVanilla;
        final boolean isEdgeCase = ArgumentEdgeCases.isArgumentEdgeCase(identifier);
        return forceWrapped || forceWrapVanilla || isEdgeCase || !isVanilla;
    }
}
