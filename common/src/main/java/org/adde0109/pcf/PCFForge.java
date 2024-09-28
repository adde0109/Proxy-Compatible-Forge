package org.adde0109.pcf;

import dev.neuralnexus.taterapi.MinecraftVersion;

import dev.neuralnexus.taterapi.Platform;
import dev.neuralnexus.taterapi.metadata.PlatformData;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(modid = "pcf", value = "pcf", acceptableRemoteVersions = "", useMetadata = true, serverSideOnly = true)
public class PCFForge {
    public PCFForge() {
        PlatformData pd = PlatformData.instance();
        pd.logger("pcf").info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + pd.minecraftVersion().toString()
                + " (" + Platform.get() + " " + pd.modLoaderVersion() + ")");

        String className = "";
        MinecraftVersion mcv = MinecraftVersion.get();
        if (mcv.isInRange(MinecraftVersion.V1_18, MinecraftVersion.V1_18_2)) {
            className = "org.adde0109.pcf.v1_18.forge.Initializer";
        } else if (mcv.is(MinecraftVersion.V1_19)) {
            className = "org.adde0109.pcf.v1_19.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersion.V1_19_1, MinecraftVersion.V1_19_2)) {
            className = "org.adde0109.pcf.v1_19_1.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersion.V1_19_3, MinecraftVersion.V1_20_4)) {
            className = "org.adde0109.pcf.v1_19_3.forge.Initializer";
        } else if (mcv.isInRange(MinecraftVersion.V1_20_5, MinecraftVersion.V1_20_6)) {
            className = "org.adde0109.pcf.v1_20_6.forge.Initializer";
        } else if (mcv.isAtLeast(MinecraftVersion.V1_21)) {
            className = "org.adde0109.pcf.v1_21.forge.Initializer";
        }
        try {
            Class.forName(className).getMethod("init").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
