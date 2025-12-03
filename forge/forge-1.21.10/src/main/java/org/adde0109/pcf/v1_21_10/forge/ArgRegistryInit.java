package org.adde0109.pcf.v1_21_10.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_21_10.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        platform = Platform.FORGE,
        mappings = Mappings.MOJANG,
        version = @Versions(min = MinecraftVersion.V20_6))
public final class ArgRegistryInit implements PCFInitializer {
    @Override
    public void onInit() {
        CSForgeBootstrap.FORGE_ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(ForgeRegistries.COMMAND_ARGUMENT_TYPES);
    }
}
