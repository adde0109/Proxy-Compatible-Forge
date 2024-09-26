package org.adde0109.pcf.v1_21.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import org.adde0109.pcf.common.CommonInitializer;

public class Initializer {
    public static void init() {
        CommonInitializer.resourceLocation = ResourceLocation::parse;
        CommonInitializer.setupIntegratedArgumentTypes();

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, org.adde0109.pcf.v1_20_2.neoforge.Initializer.configSpec);

        NeoForge.EVENT_BUS.addListener(org.adde0109.pcf.v1_20_2.neoforge.Initializer::serverAboutToStart);
    }
}
