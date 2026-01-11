package org.adde0109.pcf;

import net.neoforged.fml.common.Mod;

@Mod(PCF.MOD_ID)
public final class PCFNeo {
    public PCFNeo() {
        PCF.instance().onInit();
    }
}
