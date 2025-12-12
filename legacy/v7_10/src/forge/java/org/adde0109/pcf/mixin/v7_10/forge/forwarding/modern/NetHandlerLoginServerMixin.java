package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PACKET;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.server.network.NetHandlerLoginServer.LoginState;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.mixin.v12_2.forge.forwarding.NetworkManagerAccessor;
import org.adde0109.pcf.v7_10.forge.forwarding.modern.CCustomQueryPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.modern.INetHandlerLoginQueryServer;
import org.adde0109.pcf.v7_10.forge.forwarding.modern.SCustomQueryPacket;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Adapted
 * from Paper</a>
 */
@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Implements(@Interface(iface = INetHandlerLoginQueryServer.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin {
    // spotless:off
    @Shadow @Final private static Logger logger;
    @Shadow @Final public NetworkManager networkManager;
    @Shadow private GameProfile loginGameProfile;
    @Shadow private LoginState currentLoginState;
    @Shadow public abstract void shadow$onDisconnect(IChatComponent reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @Unique private static final ResourceLocation PLAYER_INFO_CHANNEL = new ResourceLocation("velocity:player_info");

    @Inject(method = "processLoginStart", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    private void onHandleHello(@Coerce Object packetIn, CallbackInfo ci) {
        Validate.validState(this.currentLoginState == LoginState.HELLO, "Unexpected hello packet");
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            this.networkManager.scheduleOutboundPacket(
                    new SCustomQueryPacket(
                            this.pcf$velocityLoginMessageId,
                            PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }
    // spotless:on

    public void pcf$handleCustomQuery(CCustomQueryPacket packet) {
        if (PCF.instance().forwarding().enabled()
                && packet.transactionId() == this.pcf$velocityLoginMessageId) {
            final ByteBuf buf = packet.data();
            if (buf == null) {
                this.shadow$onDisconnect(
                        new ChatComponentText(
                                "This server requires you to connect with Velocity."));
                return;
            }

            final ModernForwarding.Data data = forward(buf, this.networkManager.getRemoteAddress());
            if (data == null) {
                this.shadow$onDisconnect(new ChatComponentText(data.disconnectMsg()));
                return;
            }
            ((NetworkManagerAccessor) this.networkManager).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            // Proceed with login
            try {
                this.loginGameProfile = data.profile();
                logger.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                this.currentLoginState = LoginState.READY_TO_ACCEPT;
            } catch (Exception ex) {
                this.shadow$onDisconnect(new ChatComponentText("Failed to verify username!"));
                PCF.logger.warn("Exception verifying " + nameAndId.name(), ex);
            }
        }
    }
}
