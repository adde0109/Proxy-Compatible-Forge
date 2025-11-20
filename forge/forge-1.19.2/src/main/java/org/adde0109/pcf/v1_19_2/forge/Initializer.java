package org.adde0109.pcf.v1_19_2.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V19_2))
public final class Initializer implements PCFInitializer {
    @SuppressWarnings("deprecation")
    @Override
    public void onInit() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();

        if (MetaAPI.instance().version().isAtLeast(MinecraftVersions.V19_1)) {
            CSBootstrap.ARGUMENT_TYPES_REGISTRY = () -> Optional.of(Registry.COMMAND_ARGUMENT_TYPE);
            CSBootstrap.COMMAND_ARGUMENT_TYPE_KEY =
                    (type) -> {
                        if (CSBootstrap.isForge) {
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

            CSForgeBootstrap.FORGE_ARGUMENT_TYPES_REGISTRY =
                    () -> Optional.of(ForgeRegistries.COMMAND_ARGUMENT_TYPES);
        }
    }
}
