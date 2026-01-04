package org.adde0109.pcf.v19_2.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.crossstitch.CrossStitch;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V19_2))
public final class CrossStitchInit implements PCFInitializer {
    @SuppressWarnings("deprecation")
    @Override
    public void onInit() {
        CrossStitch.COMMAND_ARGUMENT_IDENTIFIER =
                (type) ->
                        Registry.COMMAND_ARGUMENT_TYPE
                                .getResourceKey((ArgumentTypeInfo<?, ?>) type)
                                .map(ResourceKey::location)
                                .map(ResourceLocation::toString);

        CrossStitch.COMMAND_ARGUMENT_TYPE_ID =
                (type) -> Registry.COMMAND_ARGUMENT_TYPE.getId((ArgumentTypeInfo<?, ?>) type);
    }
}
