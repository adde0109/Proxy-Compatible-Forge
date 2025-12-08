package org.adde0109.pcf.v20_4.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v17_1.forge.forwarding.FWDBootstrap;

@AConstraint(
        mappings = Mappings.SEARGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V20_4))
public final class ForwardingInit implements PCFInitializer {
    public ForwardingInit() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = Component::nullToEmpty;
        FWDBootstrap.init();
    }

    @Override
    public void onInit() {}
}
