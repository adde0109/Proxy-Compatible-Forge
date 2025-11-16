package org.adde0109.pcf.v1_19_2.forge;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@SuppressWarnings({"deprecation", "unused"})
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();

        if (MetaAPI.instance().version().isAtLeast(MinecraftVersions.V19_1)) {
            CSBootstrap.ARGUMENT_TYPES_REGISTRY = () -> Optional.of(Registry.COMMAND_ARGUMENT_TYPE);
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
        }

        ModLoadingContext context = ModLoadingContext.get();
        if (MetaAPI.instance().version().isOlderThan(MinecraftVersions.V19_2)) {
            context.registerExtensionPoint(
                    IExtensionPoint.DisplayTest.class,
                    () ->
                            new IExtensionPoint.DisplayTest(
                                    () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                                    (a, b) -> true));
        } else {
            context.registerExtensionPoint(
                    IExtensionPoint.DisplayTest.class,
                    IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION);
        }

        FMLModContainer container =
                ModList.get()
                        .getModContainerById(PCF.MOD_ID)
                        .map(FMLModContainer.class::cast)
                        .orElseThrow();
        context.registerConfig(ModConfig.Type.COMMON, Config.spec, PCF.CONFIG_FILE_NAME);

        IEventBus eventBus = container.getEventBus();
        if (eventBus == null) return;
        eventBus.addListener((ModConfigEvent.Reloading event) -> Config.reload());
    }
}
