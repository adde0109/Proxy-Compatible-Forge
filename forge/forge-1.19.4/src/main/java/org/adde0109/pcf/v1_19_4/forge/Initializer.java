package org.adde0109.pcf.v1_19_4.forge;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSBootstrap;
import org.adde0109.pcf.v1_20_4.forge.crossstitch.CSForgeBootstrap;

import java.util.Optional;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V19_3, max = MinecraftVersion.V19_4))
public final class Initializer implements PCFInitializer {
    @SuppressWarnings("deprecation")
    @Override
    public void onInit() {
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

        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();

        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION);

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
