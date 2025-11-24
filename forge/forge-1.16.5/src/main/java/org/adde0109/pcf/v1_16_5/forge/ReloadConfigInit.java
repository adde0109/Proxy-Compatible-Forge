package org.adde0109.pcf.v1_16_5.forge;

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
import org.adde0109.pcf.v1_14_4.forge.Config;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V15, max = MinecraftVersion.V16_5))
public final class ReloadConfigInit implements PCFInitializer {
    @Override
    public void onInit() {
        IEventBus eventBus =
                MetaAPI.instance()
                        .meta()
                        .<FMLModContainer>mod(PCF.MOD_ID)
                        .map(Wrapped::unwrap)
                        .orElseThrow()
                        .getEventBus();
        eventBus.addListener((ModConfig.Reloading event) -> Config.reload());
    }
}
