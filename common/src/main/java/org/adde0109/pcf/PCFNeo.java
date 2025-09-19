package org.adde0109.pcf;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(value = "pcf")
public class PCFNeo {
    Logger logger = Logger.create("pcf");

    public PCFNeo() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initNeoForge();

        MetaAPI api = MetaAPI.instance();
        MinecraftVersion mcv = api.version();
        // spotless:off
        logger.info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + mcv
                + " (" + api.platform() + " " + api.meta().loaderVersion() + ")");
        // spotless:on

        PCF.setupIntegratedArgumentTypes();

        String className;
        if (mcv.isInRange(MinecraftVersions.V20_2, MinecraftVersions.V20_6)) {
            className = "org.adde0109.pcf.v1_20_2.neoforge.Initializer";
        } else {
            className = "org.adde0109.pcf.v1_21.neoforge.Initializer";
        }
        try {
            Class.forName(className).getMethod("init").invoke(null);
        } catch (Exception e) {
            PCF.logger.error("Failed to initialize PCF", e);
        }
    }
}
