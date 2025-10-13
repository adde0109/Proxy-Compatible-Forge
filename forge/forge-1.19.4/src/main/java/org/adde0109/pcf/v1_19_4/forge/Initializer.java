package org.adde0109.pcf.v1_19_4.forge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_14_4.forge.forwarding.FWDBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@SuppressWarnings({"deprecation", "unused"})
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();
        CSBootstrap.ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
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
        CSForgeBootstrap.FORGE_ARGUMENT_TYPES_REGISTRY =
                () -> Optional.of(ForgeRegistries.COMMAND_ARGUMENT_TYPES);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);

        ModLoadingContext.get()
                .registerExtensionPoint(
                        IExtensionPoint.DisplayTest.class,
                        () ->
                                new IExtensionPoint.DisplayTest(
                                        () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                                        (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(
                (ServerAboutToStartEvent event) -> Config.setupConfig());
    }
}
