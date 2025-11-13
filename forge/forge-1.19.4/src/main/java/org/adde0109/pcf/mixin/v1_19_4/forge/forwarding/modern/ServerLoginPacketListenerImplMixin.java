package org.adde0109.pcf.mixin.v1_19_4.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PACKET;
import static org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap.COMPONENT;
import static org.adde0109.pcf.v1_17_1.forge.forwarding.FWDBootstrap.PLAYER_INFO_CHANNEL;
import static org.adde0109.pcf.v1_19_4.forge.forwarding.modern.HandleProfileKey.handle;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.common.reflection.StateUtil;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.mixin.v1_17_1.forge.network.ConnectionAccessor;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Adapted from: <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.1</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/4074d4ee99a75ad005b05bfba8257e55beeb335f/patches/server/0884-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.2</a> <br>
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Paper
 * 1.19.4</a>
 */
@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V19, max = MinecraftVersion.V20_1)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final Connection connection;
    @Shadow @Nullable public GameProfile gameProfile;
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private static final Logger pcf$LOGGER = LoggerFactory.getLogger("ServerLoginPacketListenerImpl");
    @Unique private int pcf$velocityLoginMessageId = -1;

    @Inject(method = "handleHello", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;state:Lnet/minecraft/server/network/ServerLoginPacketListenerImpl$State;"))
    // spotless:on
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            this.connection.send(
                    new ClientboundCustomQueryPacket(
                            this.pcf$velocityLoginMessageId,
                            PLAYER_INFO_CHANNEL,
                            new FriendlyByteBuf(PLAYER_INFO_PACKET)));
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && packet.getTransactionId() == this.pcf$velocityLoginMessageId) {
            QUERY_IDS.remove(this.pcf$velocityLoginMessageId);

            final FriendlyByteBuf buf = packet.getData();
            if (buf == null) {
                this.shadow$disconnect(
                        COMPONENT.apply("This server requires you to connect with Velocity."));
                ci.cancel();
                return;
            }

            // Handle general forwarding
            final ModernForwarding.Data data =
                    forward(buf, ((ConnectionAccessor) this.connection).pcf$getAddress());
            if (data == null) {
                this.shadow$disconnect(COMPONENT.apply(data.disconnectMsg()));
                ci.cancel();
                return;
            }
            ((ConnectionAccessor) this.connection).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            // Handle profile key
            final Component disconnectReason =
                    handle(
                            (ServerLoginPacketListenerImpl) (Object) this,
                            buf,
                            data.version(),
                            nameAndId.id());
            if (disconnectReason != null) {
                this.shadow$disconnect(disconnectReason);
                ci.cancel();
                return;
            }

            // TODO Update handling for lazy sessions, might not even have to do anything?

            // Proceed with login
            try {
                // TODO: Pull this into a common compat class
                if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                    this.arclight$preLogin();
                    ci.cancel();
                    return;
                }
                this.gameProfile = data.profile();
                pcf$LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                StateUtil.setState(this, 3);
            } catch (Exception ex) {
                this.shadow$disconnect(COMPONENT.apply("Failed to verify username!"));
                PCF.logger.warn("Exception verifying " + nameAndId.name(), ex);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
