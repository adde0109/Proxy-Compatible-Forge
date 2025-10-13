package org.adde0109.pcf.v1_18_2.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

import org.adde0109.pcf.v1_14_4.forge.Config;
import org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap;

@SuppressWarnings("unused")
public final class Initializer {
    public static void init() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);

        ModLoadingContext.get()
                .registerExtensionPoint(
                        IExtensionPoint.DisplayTest.class,
                        () ->
                                new IExtensionPoint.DisplayTest(
                                        () -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(
                (ServerAboutToStartEvent event) -> Config.setupConfig());
    }
}
