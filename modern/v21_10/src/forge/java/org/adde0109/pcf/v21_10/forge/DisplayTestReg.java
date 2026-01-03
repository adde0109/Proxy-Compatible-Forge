package org.adde0109.pcf.v21_10.forge;

import static org.adde0109.pcf.v17_1.forge.PCFBootstrap.IGNORE_SERVER_VERSION;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.fml.ModLoadingContext;

import org.adde0109.pcf.PCFInitializer;

@AConstraints({
    @AConstraint(platform = Platform.FORGE, version = @Versions(min = MinecraftVersion.V18_2)),
    @AConstraint(
            version =
                    @Versions({
                        MinecraftVersion.V19,
                        MinecraftVersion.V19_1,
                        MinecraftVersion.V19_3,
                        MinecraftVersion.V20_2
                    }),
            invert = true)
})
public final class DisplayTestReg implements PCFInitializer {
    @Override
    public void onInit() {
        ModLoadingContext.get().registerDisplayTest(IGNORE_SERVER_VERSION);
    }
}
