package org.adde0109.pcf.mixin.v1_20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.MAX_SUPPORTED_FORWARDING_VERSION;
import static org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap.COMPONENT;
import static org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap.PLAYER_INFO_CHANNEL;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.mixin.v1_20_2.neoforge.network.ConnectionAccessor;
import org.adde0109.pcf.v1_20_2.neoforge.Compatibility;
import org.adde0109.pcf.v1_20_2.neoforge.forwarding.modern.PlayerInfoChannelPayload;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@ReqMappings(Mappings.MOJANG)
@ReqMCVersion(min = MinecraftVersion.V20_2)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    // spotless:off
    @Shadow @Final static Logger LOGGER;
    @Shadow @Final net.minecraft.network.Connection connection;
    @Shadow abstract void shadow$startClientVerification(GameProfile profile);
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @Inject(method = "handleHello", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V"))
    // spotless:on
    private void onHandleHello(ServerboundHelloPacket packet, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            QUERY_IDS.add(this.pcf$velocityLoginMessageId);
            final ByteBuf buf = Unpooled.buffer();
            buf.writeByte(MAX_SUPPORTED_FORWARDING_VERSION);
            this.connection.send(
                    new ClientboundCustomQueryPacket(
                            this.pcf$velocityLoginMessageId,
                            new PlayerInfoChannelPayload(PLAYER_INFO_CHANNEL, buf)));
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(
            ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && packet.transactionId() == this.pcf$velocityLoginMessageId) {
            if (packet.payload() == null) {
                this.shadow$disconnect(
                        COMPONENT.apply("This server requires you to connect with Velocity."));
                ci.cancel();
                return;
            }
            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.payload().write(buf);

            Compatibility.neoForgeReadSimpleQueryPayload(buf);
            Compatibility.applyFFAPIFix(this, this.pcf$velocityLoginMessageId);

            final ModernForwarding.Data data =
                    forward(buf, ((ConnectionAccessor) this.connection).pcf$getAddress());
            if (data == null) {
                this.shadow$disconnect(COMPONENT.apply(data.disconnectMsg()));
                ci.cancel();
                return;
            }
            ((ConnectionAccessor) this.connection).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            // TODO Update handling for lazy sessions, might not even have to do anything?

            // Proceed with login
            try {
                // TODO: Pull this into a common compat class
                if (MetaAPI.instance().isPlatformPresent(Platforms.ARCLIGHT)) {
                    this.arclight$preLogin();
                    ci.cancel();
                    return;
                }
                LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                this.shadow$startClientVerification(data.profile());
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
