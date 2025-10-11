package org.adde0109.pcf.v1_20_2.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap;

import java.util.Optional;

@SuppressWarnings("unused")
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();
        CSBootstrap.ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
        CSBootstrap.COMMAND_ARGUMENT_TYPE_KEY =
                (type) ->
                        CSBootstrap.ARGUMENT_TYPES_REGISTRY
                                .get()
                                .flatMap(reg -> reg.getResourceKey(type));
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);

        NeoForge.EVENT_BUS.addListener(
                (ServerAboutToStartEvent event) -> {
                    Config.setupForwarding();
                    Config.setupModdedArgumentTypes();
                });
    }
}
