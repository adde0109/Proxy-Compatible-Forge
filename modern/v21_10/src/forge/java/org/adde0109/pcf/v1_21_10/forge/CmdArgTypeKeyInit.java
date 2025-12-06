package org.adde0109.pcf.v1_21_10.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceKey;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_21_10.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        mappings = Mappings.MOJANG,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V20_6))
public final class CmdArgTypeKeyInit implements PCFInitializer {
    public CmdArgTypeKeyInit() {
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
    }

    @Override
    public void onInit() {}
}
