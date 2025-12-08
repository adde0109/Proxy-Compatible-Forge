package org.adde0109.pcf.v1_17_1.forge;

import static org.adde0109.pcf.v1_17_1.forge.PCFBootstrap.IGNORE_SERVER_ONLY;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

import org.adde0109.pcf.PCFInitializer;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V18_1))
public final class DisplayTestInit implements PCFInitializer {
    public DisplayTestInit() {
        IGNORE_SERVER_ONLY = () -> FMLNetworkConstants.IGNORESERVERONLY;
    }

    @Override
    public void onInit() {}
}
