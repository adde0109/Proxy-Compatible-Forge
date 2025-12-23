package org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.DIRECT_CONNECT_ERR;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.FAILED_TO_VERIFY;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.adde0109.pcf.forwarding.network.ServerboundCustomQueryAnswerPacket;
import org.adde0109.pcf.mixin.v20_2.neoforge.forwarding.ConnectionAccessor;
import org.adde0109.pcf.v20_2.neoforge.Compatibility;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/server/network/ServerLoginPacketListenerImpl.java.patch">PaperMC</a>
 */
@AConstraint(mappings = Mappings.MOJANG, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final static Logger LOGGER;
    @Shadow @Final net.minecraft.network.Connection connection;
    @Shadow abstract void shadow$startClientVerification(GameProfile profile);
    @Shadow public abstract void shadow$disconnect(Component reason);

    @Inject(method = "handleHello", cancellable = true, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;startClientVerification(Lcom/mojang/authlib/GameProfile;)V"))
    // spotless:on
    private void onHandleHello(CallbackInfo ci) {
        handleHello(this, ci);
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(
            net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket mcPacket,
            CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()
                && PCF.instance().forwarding().mode().equals(Mode.MODERN)
                && mcPacket.transactionId() == this.pcf$velocityLoginMessageId()) {
            QUERY_IDS.remove(this.pcf$velocityLoginMessageId());
            final ServerboundCustomQueryAnswerPacket packet =
                    ServerboundCustomQueryAnswerPacket.fromMC(mcPacket);

            if (packet.payload() == null) {
                this.shadow$disconnect(DIRECT_CONNECT_ERR());
                ci.cancel();
                return;
            }
            final ByteBuf buf = packet.payload().data();

            Compatibility.neoForgeReadSimpleQueryPayload(buf);
            Compatibility.applyFFAPIFix(this, this.pcf$velocityLoginMessageId());

            final ModernForwarding.Data data =
                    forward(buf, ((ConnectionAccessor) this.connection).pcf$getAddress());
            if (data.disconnectMsg() != null) {
                this.shadow$disconnect(literal(data.disconnectMsg()));
                ci.cancel();
                return;
            }
            ((ConnectionAccessor) this.connection).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

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
            } catch (Exception e) {
                this.shadow$disconnect(FAILED_TO_VERIFY());
                LOGGER.error("Exception while forwarding user {}", nameAndId.name());
                e.printStackTrace();
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
