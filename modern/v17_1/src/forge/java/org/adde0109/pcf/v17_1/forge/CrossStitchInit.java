package org.adde0109.pcf.v17_1.forge;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.commands.synchronization.ArgumentTypes;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.crossstitch.CrossStitch;
import org.adde0109.pcf.mixin.v17_1.forge.crossstitch.ArgumentTypesAccessor;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V18_2))
public final class CrossStitchInit implements PCFInitializer {
    @Override
    public void onInit() {
        CrossStitch.GET_ARGUMENT_TYPE_ENTRY =
                (argumentType) -> ArgumentTypesAccessor.pcf$get((ArgumentType<?>) argumentType);

        CrossStitch.INFO_DUMP = () -> {
            PCF.logger.info("Registered Command Argument Types:");
            for (final ArgumentTypes.Entry<?> entry : ArgumentTypesAccessor.pcf$getByClass().values()) {
                PCF.logger.debug(" - " + entry.name + " -> " + entry.clazz);
            }
        };
    }
}
