package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod("pcf")
public class PCFForge {
    Logger logger = Logger.create("pcf");

    public PCFForge() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initForge();

        MetaAPI api = MetaAPI.instance();
        MinecraftVersion mcv = api.version();
        // spotless:off
        logger.info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + mcv
                + " (" + api.platform() + " " + api.meta().loaderVersion() + ")");
        // spotless:on

        PCF.setupIntegratedArgumentTypes();

        String className = "";
        if (mcv.isInRange(MinecraftVersions.V14, MinecraftVersions.V16_5)) {
            className = "org.adde0109.pcf.v1_14_4.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V17, MinecraftVersions.V17_1)) {
            className = "org.adde0109.pcf.v1_17_1.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V18, MinecraftVersions.V18_2)) {
            className = "org.adde0109.pcf.v1_18.forge.Initializer";
        } else if (mcv.is(MinecraftVersions.V19)) {
            className = "org.adde0109.pcf.v1_19.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V19_1, MinecraftVersions.V19_2)) {
            className = "org.adde0109.pcf.v1_19_1.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V19_3, MinecraftVersions.V20_4)) {
            className = "org.adde0109.pcf.v1_19_3.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersions.V20_5, MinecraftVersions.V20_6)) {
            className = "org.adde0109.pcf.v1_20_6.forge.Initializer";
        } else if (mcv.isAtLeast(MinecraftVersions.V21)) {
            className = "org.adde0109.pcf.v1_21.forge.Initializer";
        }
        try {
            Class.forName(className).getMethod("init").invoke(null);
        } catch (Exception e) {
            PCF.logger.error("Failed to initialize PCF", e);
        }
    }
}
