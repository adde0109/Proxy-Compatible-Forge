package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PACKET;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.mixin.v12_2.forge.network.ConnectionAccessor;
import org.adde0109.pcf.v12_2.forge.network.CCustomQueryPacket;
import org.adde0109.pcf.v12_2.forge.network.INetHandlerLoginQueryServer;
import org.adde0109.pcf.v12_2.forge.network.SCustomQueryPacket;
import org.adde0109.pcf.v12_2.forge.reflection.StateUtil;
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
        version = @Versions(min = MinecraftVersion.V12, max = MinecraftVersion.V12_2))
@Implements(@Interface(iface = INetHandlerLoginQueryServer.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin {
    // spotless:off
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final public NetworkManager networkManager;
    @Shadow private GameProfile loginGameProfile;
    @Shadow public abstract void shadow$onDisconnect(ITextComponent reason);

    @Unique private int pcf$velocityLoginMessageId = -1;

    @Unique private static final ResourceLocation PLAYER_INFO_CHANNEL = new ResourceLocation("velocity:player_info");

    @Inject(method = "processLoginStart", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    private void onHandleHello(CPacketLoginStart packetIn, CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (PCF.instance().forwarding().enabled()) {
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            this.networkManager.sendPacket(
                    new CCustomQueryPacket(
                            this.pcf$velocityLoginMessageId,
                            PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }
    // spotless:on

    public void pcf$handleCustomQuery(SCustomQueryPacket packet) {
        if (PCF.instance().forwarding().enabled()
                && packet.getTransactionId() == this.pcf$velocityLoginMessageId) {
            final ByteBuf buf = packet.getData();
            if (buf == null) {
                this.shadow$onDisconnect(
                        new TextComponentString(
                                "This server requires you to connect with Velocity."));
                return;
            }

            final ModernForwarding.Data data = forward(buf, this.networkManager.getRemoteAddress());
            if (data == null) {
                this.shadow$onDisconnect(new TextComponentString(data.disconnectMsg()));
                return;
            }
            ((ConnectionAccessor) this.networkManager).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            // Proceed with login
            try {
                this.loginGameProfile = data.profile();
                LOGGER.info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
                StateUtil.setState(this, 3);
            } catch (Exception ex) {
                this.shadow$onDisconnect(new TextComponentString("Failed to verify username!"));
                PCF.logger.warn("Exception verifying " + nameAndId.name(), ex);
            }
        }
    }
}
