package org.adde0109.pcf;

import dev.neuralnexus.taterapi.meta.platforms.TaterMetadata;

import net.neoforged.fml.common.Mod;

@Mod(PCF.MOD_ID)
public final class PCFNeo {
    public PCFNeo() {
        // Bootstrap TaterLibLite Metadata
        TaterMetadata.initNeoForge();

        PCF.instance().onInit();
    }
}
