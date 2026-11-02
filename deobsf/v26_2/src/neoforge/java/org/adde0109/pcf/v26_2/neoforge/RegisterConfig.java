package org.adde0109.pcf.v26_2.neoforge;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;

@AConstraint(platform = Platform.NEOFORGE, version = @Versions(min = MinecraftVersion.V21))
public final class RegisterConfig implements PCFInitializer {
    @Override
    public void onInit() {
        final ModContainer container =
                MetaAPI.instance()
                        .meta()
                        .<ModContainer>mod(PCF.MOD_ID)
                        .map(Wrapped::unwrap)
                        .orElseThrow();
        container.registerConfig(Config.configType(), Config.spec, PCF.CONFIG_FILE_NAME);
    }
}
