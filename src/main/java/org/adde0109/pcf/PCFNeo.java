package org.adde0109.pcf;

import dev.neuralnexus.taterapi.MinecraftVersion;

import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(value = "pcf")//, dist = Dist.DEDICATED_SERVER)
public class PCFNeo {
    public PCFNeo() {
        if (MinecraftVersion.get().isInRange(MinecraftVersion.V1_20_2, MinecraftVersion.V1_20_6)) {
            org.adde0109.pcf.v1_20_2.neoforge.Initializer.init();
        } else {
            org.adde0109.pcf.v1_21.neoforge.Initializer.init();
        }
    }
}
