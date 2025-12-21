package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.text.ITextComponent;

import org.adde0109.pcf.v12_2.forge.forwarding.modern.NetHandlerLoginServerBridge;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V9, max = MinecraftVersion.V12_2))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin_Impl implements NetHandlerLoginServerBridge {
    // spotless:off
    @Shadow @Final private static Logger LOGGER;
    @Shadow public abstract void shadow$onDisconnect(ITextComponent reason);
    // spotless:on

    @Override
    public void bridge$onDisconnect(Object reason) {
        this.shadow$onDisconnect((ITextComponent) reason);
    }

    @Override
    public void bridge$logger_info(String text, Object... params) {
        LOGGER.info(text, params);
    }
}
