package org.adde0109.pcf.v21_11;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.crossstitch.CrossStitch;

import java.util.Map;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V21_11))
public final class CrossStitchInit implements PCFInitializer {
    public CrossStitchInit() {
        CrossStitch.COMMAND_ARGUMENT_IDENTIFIER =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE
                                .getResourceKey((ArgumentTypeInfo<?, ?>) type)
                                .map(ResourceKey::identifier)
                                .map(Identifier::toString);

        CrossStitch.COMMAND_ARGUMENT_TYPE_ID =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(
                                (ArgumentTypeInfo<?, ?>) type);

        CrossStitch.INFO_DUMP =
                () -> {
                    PCF.logger.info("Registered Command Argument Types:");
                    for (final Map.Entry<
                                    ResourceKey<ArgumentTypeInfo<?, ?>>, ArgumentTypeInfo<?, ?>>
                            entry : BuiltInRegistries.COMMAND_ARGUMENT_TYPE.entrySet()) {
                        final Identifier identifier = entry.getKey().identifier();
                        final int id =
                                BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(entry.getValue());
                        PCF.logger.debug(" - " + identifier + " -> " + id);
                    }
                };
    }

    @Override
    public void onInit() {}
}
