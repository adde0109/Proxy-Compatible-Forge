package org.adde0109.pcf.v20_2.neoforge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v20_2.neoforge.forwarding.FWDBootstrap;

@AConstraint(
        mappings = Mappings.MOJANG,
        version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4))
public final class ResourceLocationInit implements PCFInitializer {
    public ResourceLocationInit() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
    }

    @Override
    public void onInit() {}
}
