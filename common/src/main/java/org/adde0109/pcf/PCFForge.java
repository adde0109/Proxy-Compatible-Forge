package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(PCF.MOD_ID)
public class PCFForge {
    Logger logger = Logger.create(PCF.MOD_ID);

    public PCFForge() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initForge();

        MetaAPI api = MetaAPI.instance();
        MinecraftVersion mcv = api.version();
        // spotless:off
        logger.info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + mcv
                + " (" + api.platform() + " " + api.meta().apiVersion() + ")");
        // spotless:on

        String className = "";
        if (mcv.isInRange(MinecraftVersions.V14, MinecraftVersions.V16_5)) {
            className = "org.adde0109.pcf.v1_14_4.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V17, MinecraftVersions.V17_1)) {
            className = "org.adde0109.pcf.v1_17_1.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V18, MinecraftVersions.V18_2)) {
            className = "org.adde0109.pcf.v1_18_2.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V19, MinecraftVersions.V19_2)) {
            className = "org.adde0109.pcf.v1_19_2.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V19_3, MinecraftVersions.V19_4)) {
            className = "org.adde0109.pcf.v1_19_4.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V20, MinecraftVersions.V20_4)) {
            className = "org.adde0109.pcf.v1_20_4.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V20_5, MinecraftVersions.V20_6)) {
            className = "org.adde0109.pcf.v1_20_6.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V21, MinecraftVersions.V21_5)) {
            className = "org.adde0109.pcf.v1_21_5.forge.Initializer";
        } else if (mcv.isAtLeast(MinecraftVersions.V21_6)) {
            className = "org.adde0109.pcf.v1_21_10.forge.Initializer";
        }
        try {
            Class.forName(className).getMethod("init").invoke(null);
        } catch (Exception e) {
            PCF.logger.error("Failed to initialize PCF", e);
        }
    }
}
