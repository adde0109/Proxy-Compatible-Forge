package org.adde0109.pcf;

import dev.neuralnexus.taterapi.MinecraftVersion;

import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(modid = "pcf", value = "pcf", acceptableRemoteVersions = "", useMetadata = true, serverSideOnly = true)
public class PCFForge {
    public PCFForge() {
        MinecraftVersion mcv = MinecraftVersion.get();
        if (mcv.isInRange(MinecraftVersion.V1_20_5, MinecraftVersion.V1_20_6)) {
            org.adde0109.pcf.v1_20_6.forge.Initializer.init();
        } else if (mcv.isAtLeast(MinecraftVersion.V1_21)) {
            org.adde0109.pcf.v1_21.forge.Initializer.init();
        }
    }
}
