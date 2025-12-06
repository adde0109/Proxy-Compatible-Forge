package org.adde0109.pcf.v1_20_2.neoforge;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;

@AConstraint(
        platform = Platform.NEOFORGE,
        version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_6))
public final class RegisterConfig implements PCFInitializer {
    @Override
    public void onInit() {
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, Config.spec, PCF.CONFIG_FILE_NAME);
    }
}
