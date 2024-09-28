package org.adde0109.pcf;

import dev.neuralnexus.taterapi.MinecraftVersion;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(modid = "pcf", value = "pcf", acceptableRemoteVersions = "", useMetadata = true, serverSideOnly = true)
public class PCFForge {
    public PCFForge() {
        String className = "";
        MinecraftVersion mcv = MinecraftVersion.get();
        if (mcv.isInRange(MinecraftVersion.V1_20_2, MinecraftVersion.V1_20_4)) {
            className = "org.adde0109.pcf.v1_20_2.forge.Initializer";
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
