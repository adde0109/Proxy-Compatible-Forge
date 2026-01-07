package org.adde0109.pcf.mixin.v16_5.forge.crossstitch;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.commands.synchronization.ArgumentTypes;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
@Mixin(ArgumentTypes.class)
public interface ArgumentTypesAccessor {
    @Invoker("get")
    static <T extends ArgumentType<?>> ArgumentTypes.@Nullable Entry<T> pcf$get(T argumentType) {
        throw new UnsupportedOperationException();
    }

    @Accessor("BY_CLASS")
    static <T extends ArgumentType<?>>
            Map<Class<? extends T>, ArgumentTypes.Entry<T>> pcf$getByClass() {
        throw new UnsupportedOperationException();
    }
}
