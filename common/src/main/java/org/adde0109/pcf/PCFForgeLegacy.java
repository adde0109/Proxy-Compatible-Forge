package org.adde0109.pcf;

import cpw.mods.fml.common.Mod;

import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

@Mod(modid = PCF.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public final class PCFForgeLegacy {
    public PCFForgeLegacy() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initForge();

        PCF.instance().onInit();
    }
}
