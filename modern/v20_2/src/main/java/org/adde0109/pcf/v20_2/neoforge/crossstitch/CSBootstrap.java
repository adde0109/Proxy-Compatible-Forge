package org.adde0109.pcf.v20_2.neoforge.crossstitch;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CSBootstrap {
    public static Supplier<Optional<Registry<ArgumentTypeInfo<?, ?>>>> ARGUMENT_TYPES_REGISTRY =
            Optional::empty;
    public static Function<ArgumentTypeInfo<?, ?>, Optional<ResourceKey<ArgumentTypeInfo<?, ?>>>>
            COMMAND_ARGUMENT_TYPE_KEY = (type) -> Optional.empty();
    public static Function<ArgumentTypeInfo<?, ?>, Integer> COMMAND_ARGUMENT_TYPE_ID = (type) -> -1;

    public static Optional<ResourceKey<ArgumentTypeInfo<?, ?>>> commandArgumentResourceKey(
            ArgumentTypeInfo<?, ?> type) {
        return COMMAND_ARGUMENT_TYPE_KEY.apply(type);
    }

    public static int commandArgumentTypeId(ArgumentTypeInfo<?, ?> type) {
        return COMMAND_ARGUMENT_TYPE_ID.apply(type);
    }
}
