package org.adde0109.pcf;

import dev.neuralnexus.taterapi.MinecraftVersion;

import dev.neuralnexus.taterapi.Platform;
import dev.neuralnexus.taterapi.metadata.PlatformData;

import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(value = "pcf")//, dist = Dist.DEDICATED_SERVER)
public class PCFNeo {
    public PCFNeo() {
        PlatformData pd = PlatformData.instance();
        pd.logger("pcf").info("Initializing Proxy Compatible Forge on "
                + "Minecraft " + pd.minecraftVersion().toString()
                + " (" + Platform.get() + " " + pd.modLoaderVersion() + ")");

        String className;
        if (MinecraftVersion.get().isInRange(MinecraftVersion.V1_20_2, MinecraftVersion.V1_20_6)) {
            className = "org.adde0109.pcf.v1_20_2.neoforge.Initializer";
        } else {
            className = "org.adde0109.pcf.v1_21.neoforge.Initializer";
        }
        try {
            Class.forName(className).getMethod("init").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
