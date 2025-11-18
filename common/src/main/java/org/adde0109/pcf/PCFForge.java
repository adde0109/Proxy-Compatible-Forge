package org.adde0109.pcf;

import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.minecraftforge.fml.common.Mod;

@Mod(PCF.MOD_ID)
public final class PCFForge {
    public PCFForge() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initForge();

        PCF.instance().onInit();
    }
}
