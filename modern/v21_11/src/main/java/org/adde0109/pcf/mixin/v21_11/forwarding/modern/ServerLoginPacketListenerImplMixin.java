package org.adde0109.pcf.mixin.v21_11.forwarding.modern;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final Connection connection;
    @Shadow @Final static Logger LOGGER;

    @AConstraint(version = @Versions(min = MinecraftVersion.V21))
    @Shadow public abstract void shadow$onDisconnect(net.minecraft.network.DisconnectionDetails details);

    @Shadow abstract void shadow$startClientVerification(GameProfile profile);
    @Unique private int pcf$velocityLoginMessageId = -1;
    // spotless:on

    @Override
    public int bridge$velocityLoginMessageId() {
        return this.pcf$velocityLoginMessageId;
    }

    @Override
    public void bridge$setVelocityLoginMessageId(final int id) {
        this.pcf$velocityLoginMessageId = id;
    }

    @Override
    public @NonNull ConnectionBridge bridge$connection() {
        return (ConnectionBridge) this.connection;
    }

    @AConstraint(version = @Versions(min = MinecraftVersion.V21))
    @Override
    public void bridge$disconnect(final @NonNull Object reason) {
        this.shadow$onDisconnect(
                new net.minecraft.network.DisconnectionDetails((Component) reason));
    }

    @Override
    public void bridge$startClientVerification(final @NonNull GameProfile profile) {
        this.shadow$startClientVerification(profile);
    }

    @Override
    public void bridge$logger_info(final @NonNull String text, final Object... params) {
        LOGGER.info(text, params);
    }

    @Override
    public void bridge$logger_error(final @NonNull String text, final Object... params) {
        LOGGER.error(text, params);
    }
}
