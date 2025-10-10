package org.adde0109.pcf.v1_20_6.forge.crossstitch;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Optional;
import java.util.function.Supplier;

public final class CSForgeBootstrap {
    public static Supplier<Optional<IForgeRegistry<ArgumentTypeInfo<?, ?>>>>
            FORGE_ARGUMENT_TYPES_REGISTRY = Optional::empty;

    public static Optional<ResourceKey<ArgumentTypeInfo<?, ?>>> getKey(
            ArgumentTypeInfo<?, ?> type) {
        return FORGE_ARGUMENT_TYPES_REGISTRY.get().flatMap(reg -> reg.getResourceKey(type));
    }
}
