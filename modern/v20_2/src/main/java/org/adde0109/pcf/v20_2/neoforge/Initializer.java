package org.adde0109.pcf.v20_2.neoforge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.core.registries.BuiltInRegistries;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.forwarding.network.codec.adapter.AdapterRegistry;
import org.adde0109.pcf.v20_2.neoforge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v20_2.neoforge.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v20_2.neoforge.forwarding.network.SCustomQueryAnswerPacketAdapter;

import java.util.Optional;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
public final class Initializer implements PCFInitializer {
    public Initializer() {
        AdapterRegistry.register(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);
    }

    @Override
    public void onInit() {
        CSBootstrap.ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

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
}
