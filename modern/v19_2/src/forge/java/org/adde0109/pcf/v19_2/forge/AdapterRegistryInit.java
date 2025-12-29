package org.adde0109.pcf.v19_2.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v19_2.forge.forwarding.modern.ProfilePublicKeyDataAdapter;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V19, max = MinecraftVersion.V19_2))
public final class AdapterRegistryInit implements PCFInitializer {
    public AdapterRegistryInit() {
        PCF.instance().adapters().register(ProfilePublicKeyDataAdapter.INSTANCE);
    }

    @Override
    public void onInit() {}
}
