package org.adde0109.pcf.v21_10;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.network.NetworkAdapters;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.crossstitch.CrossStitch;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.v20_2.neoforge.Compatibility;
import org.adde0109.pcf.v21_10.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v21_10.forwarding.network.SCustomQueryAnswerPacketAdapter;

import java.util.Map;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
public final class Initializer implements PCFInitializer {
    public Initializer() {
        NetworkAdapters.register(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);
        if (Compatibility.NEOFORGE_V20_2.result()) {
            ModernForwarding.preProcessor = Compatibility::neoForgeReadSimpleQueryPayload;
        } else if (Compatibility.FFAPI_V21_1.result()) {
            ModernForwarding.preProcessor = Compatibility::applyFFAPIFix;
        }

        CrossStitch.COMMAND_ARGUMENT_IDENTIFIER =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE
                                .getResourceKey((ArgumentTypeInfo<?, ?>) type)
                                .map(ResourceKey::location)
                                .map(ResourceLocation::toString);

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
                        final ResourceLocation identifier = entry.getKey().location();
                        final int id =
                                BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(entry.getValue());
                        PCF.logger.debug(" - " + identifier + " -> " + id);
                    }
                };
    }

    @Override
    public void onInit() {}
}
