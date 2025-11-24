package org.adde0109.pcf.v1_14_4.forge;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V14_4))
public final class ReloadConfigInit implements PCFInitializer {
    @Override
    public void onInit() {
        IEventBus eventBus =
                MetaAPI.instance()
                        .meta()
                        .<FMLModContainer>mod(PCF.MOD_ID)
                        .map(Wrapped::unwrap)
                        .map(FMLModContainer::getEventBus)
                        .orElseThrow();
        eventBus.addListener((ModConfig.ConfigReloading event) -> Config.reload());
    }
}
