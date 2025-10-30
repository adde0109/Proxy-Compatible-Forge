package org.adde0109.pcf.v1_16_5.forge;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_14_4.forge.forwarding.FWDBootstrap;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("unused")
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = TextComponent::new;
        FWDBootstrap.init();

        ModLoadingContext context = ModLoadingContext.get();

        context.registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST,
                () ->
                        Pair.of(
                                () -> FMLNetworkConstants.IGNORESERVERONLY,
                                (remoteVersion, isFromServer) -> true));

        FMLModContainer container =
                ModList.get()
                        .getModContainerById(PCF.MOD_ID)
                        .map(FMLModContainer.class::cast)
                        .orElseThrow();
        context.registerConfig(ModConfig.Type.COMMON, Config.spec, PCF.CONFIG_FILE_NAME);

        IEventBus eventBus = container.getEventBus();
        if (eventBus == null) return;
        eventBus.addListener((ModConfig.Reloading event) -> Config.reload());
    }
}
