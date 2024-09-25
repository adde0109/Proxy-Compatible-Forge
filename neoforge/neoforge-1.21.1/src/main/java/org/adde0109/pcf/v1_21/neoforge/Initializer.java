package org.adde0109.pcf.v1_21.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.adde0109.pcf.common.RLHelper;

@Mod(value = "pcf", dist = Dist.DEDICATED_SERVER)
public class Initializer {
  public Initializer() {
    RLHelper.resourceLocation = ResourceLocation::parse;

    ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, org.adde0109.pcf.Initializer.configSpec);

    NeoForge.EVENT_BUS.addListener(org.adde0109.pcf.Initializer::serverAboutToStart);

    org.adde0109.pcf.Initializer.setupIntegratedArgumentTypes();
  }
}
