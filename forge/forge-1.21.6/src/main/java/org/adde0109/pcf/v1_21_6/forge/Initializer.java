package org.adde0109.pcf.v1_21_6.forge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.v1_14_4.forge.Config;

@SuppressWarnings("unused")
public class Initializer {
    //    public static void init(FMLJavaModLoadingContext context) {
    public static void init() {
        PCF.resourceLocation = ResourceLocation::parse;
        PCF.component = Component::nullToEmpty;
        PCF.COMMAND_ARGUMENT_TYPE_KEY =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(
                                (ArgumentTypeInfo<?, ?>) type);
        PCF.COMMAND_ARGUMENT_TYPE_ID =
                (type) ->
                        BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(
                                (ArgumentTypeInfo<?, ?>) type);

        // TODO: Upstream the additions into entrypoint-spoof
        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, Config.spec);

        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () ->
                        new IExtensionPoint.DisplayTest(
                                () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                                (a, b) -> true));

        ServerAboutToStartEvent.BUS.addListener(
                (ServerAboutToStartEvent event) -> {
                    Config.setupForwarding();
                    Config.setupModdedArgumentTypes();
                });
    }
}
