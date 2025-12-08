package org.adde0109.pcf.v20_2.neoforge;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.config.ModConfigEvent;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;

@AConstraint(platform = Platform.NEOFORGE, version = @Versions(min = MinecraftVersion.V20_2))
public final class ReloadConfigInit implements PCFInitializer {
    @Override
    public void onInit() {
        IEventBus eventBus =
                MetaAPI.instance()
                        .meta()
                        .<ModContainer>mod(PCF.MOD_ID)
                        .map(Wrapped::unwrap)
                        .map(ModContainer::getEventBus)
                        .orElseThrow();
        eventBus.addListener((ModConfigEvent.Reloading event) -> Config.reload());
    }
}
