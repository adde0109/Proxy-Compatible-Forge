package org.adde0109.pcf.v20_4.forge.crossstitch;

import dev.neuralnexus.taterapi.meta.MetaAPI;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.crossstitch.compat.ArgumentEdgeCases;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CSBootstrap {
    public static Supplier<Optional<Registry<ArgumentTypeInfo<?, ?>>>> ARGUMENT_TYPES_REGISTRY =
            Optional::empty;
    public static Function<ArgumentTypeInfo<?, ?>, Optional<ResourceKey<ArgumentTypeInfo<?, ?>>>>
            COMMAND_ARGUMENT_TYPE_KEY = (type) -> Optional.empty();
    public static Function<ArgumentTypeInfo<?, ?>, Integer> COMMAND_ARGUMENT_TYPE_ID = (type) -> -1;

    public static final boolean isForge = MetaAPI.instance().platform().isForge();

    public static Optional<ResourceKey<ArgumentTypeInfo<?, ?>>> commandArgumentResourceKey(
            ArgumentTypeInfo<?, ?> type) {
        return COMMAND_ARGUMENT_TYPE_KEY.apply(type);
    }

    public static int commandArgumentTypeId(ArgumentTypeInfo<?, ?> type) {
        return COMMAND_ARGUMENT_TYPE_ID.apply(type);
    }

    private static final Set<String> BUILT_IN_REGISTRY_KEYS = Set.of("minecraft", "brigadier");

    public static boolean shouldWrapArgument(ResourceLocation identifier) {
        boolean isVanilla =
                BUILT_IN_REGISTRY_KEYS.stream().anyMatch(identifier.getNamespace()::equals);
        boolean forceWrapped =
                PCF.instance().crossStitch().forceWrappedArguments().stream()
                        .anyMatch(identifier.toString()::equals);
        boolean forceWrapVanilla =
                PCF.instance().crossStitch().forceWrapVanillaArguments() && isVanilla;
        boolean isEdgeCase = ArgumentEdgeCases.isArgumentEdgeCase(identifier.toString());
        return forceWrapped || forceWrapVanilla || isEdgeCase || !isVanilla;
    }
}
