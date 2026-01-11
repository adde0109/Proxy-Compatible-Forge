package org.adde0109.pcf.v21_11;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.network.NetworkRegistry;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.v20_2.neoforge.Compatibility;
import org.adde0109.pcf.v21_11.forwarding.network.CCustomQueryPacketAdapter;
import org.adde0109.pcf.v21_11.forwarding.network.SCustomQueryAnswerPacketAdapter;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
public final class AdapterRegistryInit implements PCFInitializer {
    public AdapterRegistryInit() {
        NetworkRegistry.registerAdapter(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);
        if (Compatibility.NEOFORGE_V20_2.result()) {
            ModernForwarding.preProcessor = Compatibility::neoForgeReadSimpleQueryPayload;
        } else if (Compatibility.FFAPI_V21_1.result()) {
            ModernForwarding.preProcessor = Compatibility::applyFFAPIFix;
        }
    }

    @Override
    public void onInit() {}
}
