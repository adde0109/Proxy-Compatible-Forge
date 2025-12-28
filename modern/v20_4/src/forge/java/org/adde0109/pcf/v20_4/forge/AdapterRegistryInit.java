package org.adde0109.pcf.v20_4.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v20_4.forge.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v20_4.forge.forwarding.network.SCustomQueryAnswerPacketAdapter;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4))
public final class AdapterRegistryInit implements PCFInitializer {
    public AdapterRegistryInit() {
        PCF.instance()
                .adapters()
                .register(
                        CCustomQueryPacketAdapter.INSTANCE,
                        SCustomQueryAnswerPacketAdapter.INSTANCE);
    }

    @Override
    public void onInit() {}
}
