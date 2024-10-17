package org.adde0109.pcf.v1_17_1.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.Config;

@SuppressWarnings("unused")
public class Initializer {
    public static void init() {
        PCF.resourceLocation = ResourceLocation::new;
        PCF.component = Component::nullToEmpty;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);

        ModLoadingContext.get()
                .registerExtensionPoint(
                        IExtensionPoint.DisplayTest.class,
                        () ->
                                new IExtensionPoint.DisplayTest(
                                        () -> FMLNetworkConstants.IGNORESERVERONLY,
                                        (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(
                (FMLServerAboutToStartEvent event) -> {
                    Config.setupForwarding();
                    Config.setupModdedArgumentTypes();
                });
    }
}
