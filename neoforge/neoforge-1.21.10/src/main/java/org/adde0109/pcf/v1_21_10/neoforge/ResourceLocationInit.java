package org.adde0109.pcf.v1_21_10.neoforge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_5))
public final class ResourceLocationInit implements PCFInitializer {
    public ResourceLocationInit() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::parse;
    }

    @Override
    public void onInit() {}
}
