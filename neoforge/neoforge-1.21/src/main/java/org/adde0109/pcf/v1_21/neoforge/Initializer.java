package org.adde0109.pcf.v1_21.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_20_2.neoforge.Config;
import org.adde0109.pcf.v1_20_2.neoforge.crossstitch.CSBootstrap;

import java.util.Optional;

@SuppressWarnings("unused")
public class Initializer {
    public static void init() {
        PCF.resourceLocation = ResourceLocation::parse;
        PCF.component = Component::nullToEmpty;
        CSBootstrap.ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

        ModLoadingContext.get()
                .getActiveContainer()
                .registerConfig(ModConfig.Type.COMMON, Config.spec);

        NeoForge.EVENT_BUS.addListener(
                (ServerAboutToStartEvent event) -> {
                    Config.setupForwarding();
                    Config.setupModdedArgumentTypes();
                });
    }
}
