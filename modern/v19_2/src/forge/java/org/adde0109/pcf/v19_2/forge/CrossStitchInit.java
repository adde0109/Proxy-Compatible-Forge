package org.adde0109.pcf.v19_2.forge;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceKey;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2))
public final class CrossStitchInit implements PCFInitializer {
    public CrossStitchInit() {
        CSBootstrap.COMMAND_ARGUMENT_TYPE_KEY =
                (type) -> {
                    if (Constraint.builder().platform(Platforms.FORGE).result()) {
                        Optional<ResourceKey<ArgumentTypeInfo<?, ?>>> entry =
                                CSForgeBootstrap.getKey(type);
                        if (entry.isPresent()) {
                            return entry;
                        }
                    }
                    return CSBootstrap.ARGUMENT_TYPES_REGISTRY
                            .get()
                            .flatMap(reg -> reg.getResourceKey(type));
                };
        CSBootstrap.COMMAND_ARGUMENT_TYPE_ID =
                (type) ->
                        CSBootstrap.ARGUMENT_TYPES_REGISTRY
                                .get()
                                .map(reg -> reg.getId(type))
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "Could not find ID for argument type: "
                                                                + type.getClass().getName()));
    }

    @Override
    public void onInit() {}
}
