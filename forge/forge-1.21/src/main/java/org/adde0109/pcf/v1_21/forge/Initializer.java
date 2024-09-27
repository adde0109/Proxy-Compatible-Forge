package org.adde0109.pcf.v1_21.forge;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.ForgeRegistries;
import org.adde0109.pcf.common.CommonInitializer;

public class Initializer {
//    public static void init(FMLJavaModLoadingContext context) {
    public static void init() {
        CommonInitializer.resourceLocation = ResourceLocation::parse;
        CommonInitializer.COMMAND_ARGUMENT_TYPE = (type) -> ForgeRegistries.COMMAND_ARGUMENT_TYPES.getKey((ArgumentTypeInfo<?, ?>) type);
        CommonInitializer.setupIntegratedArgumentTypes();

        // TODO: Upstream the additions into entrypoint-spoof
        ModLoadingContext context = ModLoadingContext.get();

        context.registerConfig(ModConfig.Type.COMMON, org.adde0109.pcf.v1_20_6.forge.Initializer.configSpec);

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(org.adde0109.pcf.v1_20_6.forge.Initializer::serverAboutToStart);
    }
}
