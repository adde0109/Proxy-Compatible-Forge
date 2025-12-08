package org.adde0109.pcf.v17_1.forge;

import static org.adde0109.pcf.v17_1.forge.PCFBootstrap.IGNORE_SERVER_VERSION;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import org.adde0109.pcf.PCFInitializer;

@AConstraint(
        platform = Platform.FORGE,
        version =
                @Versions(
                        value = {
                            MinecraftVersion.V19,
                            MinecraftVersion.V19_3,
                            MinecraftVersion.V20_2
                        },
                        min = MinecraftVersion.V17,
                        max = MinecraftVersion.V18_1))
public final class DisplayTestReg implements PCFInitializer {
    @Override
    public void onInit() {
        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class, IGNORE_SERVER_VERSION);
    }
}
