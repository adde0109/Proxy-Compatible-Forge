package org.adde0109.pcf.v1_17_1.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap;

@SuppressWarnings("unused")
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();

        ModLoadingContext context = ModLoadingContext.get();
        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () ->
                        new IExtensionPoint.DisplayTest(
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
        eventBus.addListener((ModConfigEvent.Reloading event) -> Config.reload());
    }
}
