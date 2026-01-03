package org.adde0109.pcf.v19_2.forge;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.network.NetworkAdapters;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v19_2.forge.forwarding.modern.ProfilePublicKeyDataAdapter;
import org.adde0109.pcf.v19_2.forge.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v19_2.forge.forwarding.network.SCustomQueryAnswerPacketAdapter;

@AConstraint(
        mappings = Mappings.SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V17, max = MinecraftVersion.V20_1))
public final class AdapterRegistryInit implements PCFInitializer {
    public AdapterRegistryInit() {
        if (Constraint.range(MinecraftVersions.V19, MinecraftVersions.V19_2).result()) {
            PCF.instance().adapters().register(ProfilePublicKeyDataAdapter.INSTANCE);
        }
        NetworkAdapters.register(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);
    }

    @Override
    public void onInit() {}
}
