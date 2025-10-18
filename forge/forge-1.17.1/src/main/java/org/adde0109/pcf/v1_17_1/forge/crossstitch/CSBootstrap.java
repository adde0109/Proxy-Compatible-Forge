package org.adde0109.pcf.v1_17_1.forge.crossstitch;

import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.crossstitch.compat.ArgumentEdgeCases;

import java.util.Set;

public final class CSBootstrap {
    private static final Set<String> BUILT_IN_REGISTRY_KEYS = Set.of("minecraft", "brigadier");

    public static boolean shouldWrapArgument(ResourceLocation identifier) {
        return PCF.instance().crossStitch().forceWrappedArguments().stream()
                        .anyMatch(identifier.toString()::equals)
                || !(BUILT_IN_REGISTRY_KEYS.stream().anyMatch(identifier.getNamespace()::equals)
                        || ArgumentEdgeCases.isArgumentEdgeCase(identifier.toString()));
    }
}
