package org.adde0109.pcf.mixin.v16_5.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleCustomQueryPacket;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@AConstraint(mappings = Mappings.LEGACY_SEARGE, version = @Versions(min = MinecraftVersion.V14))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final public Connection connection;
    @Shadow @Nullable private GameProfile gameProfile;
    @Shadow private ServerLoginPacketListenerImpl.State state;
    @Shadow @Final private static Logger LOGGER;
    @Shadow public abstract void shadow$onDisconnect(Component reason);
    @Unique private int pcf$velocityLoginMessageId = -1;
    // spotless:on

    // spotless:off
    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    // spotless:on
    private void onHandleHello(final @NotNull CallbackInfo ci) {
        handleHello(this, ci);
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(
            final @NotNull ServerboundCustomQueryPacket packet, final @NotNull CallbackInfo ci) {
        handleCustomQueryPacket(
                this,
                ((ServerboundCustomQueryPacketAccessor) packet).pcf$getTransactionId(),
                packet,
                ci);
    }

    @Override
    public int bridge$velocityLoginMessageId() {
        return this.pcf$velocityLoginMessageId;
    }

    @Override
    public void bridge$setVelocityLoginMessageId(final int id) {
        this.pcf$velocityLoginMessageId = id;
    }

    @Override
    public @NotNull ConnectionBridge bridge$connection() {
        return (ConnectionBridge) this.connection;
    }

    @Override
    public void bridge$disconnect(final @NotNull Object reason) {
        this.shadow$onDisconnect((Component) reason);
    }

    @Override
    public void bridge$startClientVerification(final @NotNull GameProfile profile) {
        this.gameProfile = profile;
        this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
    }

    @Override
    public void bridge$logger_info(final @NotNull String text, final Object... params) {
        LOGGER.info(text, params);
    }

    @Override
    public void bridge$logger_error(final @NotNull String text, final Object... params) {
        LOGGER.error(text, params);
    }
}
