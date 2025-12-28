package org.adde0109.pcf;

import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.minecraftforge.fml.common.Mod;

@Mod(
        value = PCF.MOD_ID,
        modid = PCF.MOD_ID,
        useMetadata = true,
        serverSideOnly = true,
        acceptableRemoteVersions = "*")
public final class PCFForge {
    public PCFForge() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initForge();

        PCF.instance().onInit();
    }
}
