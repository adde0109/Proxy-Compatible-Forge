package org.adde0109.pcf.v19_2.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.core.Registry;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V19_1, max = MinecraftVersion.V19_2))
public final class ArgRegistryInit implements PCFInitializer {
    @SuppressWarnings("deprecation")
    @Override
    public void onInit() {
        CSBootstrap.ARGUMENT_TYPES_REGISTRY = () -> Optional.of(Registry.COMMAND_ARGUMENT_TYPE);
        CSForgeBootstrap.FORGE_ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(ForgeRegistries.COMMAND_ARGUMENT_TYPES);
    }
}
