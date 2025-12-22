package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;

import org.adde0109.pcf.v12_2.forge.forwarding.modern.NetHandlerLoginServerBridge;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V8_9))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin_Impl implements NetHandlerLoginServerBridge {
    // spotless:off
    @Shadow @Final private static Logger logger;
    @Shadow public abstract void shadow$onDisconnect(IChatComponent reason);
    // spotless:on

    @Override
    public void bridge$onDisconnect(Object reason) {
        this.shadow$onDisconnect((IChatComponent) reason);
    }

    @Override
    public void bridge$logger_info(String text, Object... params) {
        logger.info(text, params);
    }
}
