package org.adde0109.pcf.v1_14_4.forge;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v1_14_4.forge.forwarding.FWDBootstrap;

@AConstraint(
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V14_4))
public final class Initializer implements PCFInitializer {
    @Override
    public void onInit() {
        FWDBootstrap.RESOURCE_LOCATION = ResourceLocation::new;
        FWDBootstrap.COMPONENT = TextComponent::new;
        FWDBootstrap.init();

        FMLModContainer container =
                ModList.get()
                        .getModContainerById(PCF.MOD_ID)
                        .map(FMLModContainer.class::cast)
                        .orElseThrow();

        IEventBus eventBus = container.getEventBus();
        if (eventBus == null) return;
        eventBus.addListener((ModConfig.ConfigReloading event) -> Config.reload());
    }
}
