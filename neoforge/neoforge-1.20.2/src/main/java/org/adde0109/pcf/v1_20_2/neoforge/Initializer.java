package org.adde0109.pcf.v1_20_2.neoforge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap;

import java.util.Optional;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
public final class Initializer implements PCFInitializer {
    @Override
    public void onInit() {
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();

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
