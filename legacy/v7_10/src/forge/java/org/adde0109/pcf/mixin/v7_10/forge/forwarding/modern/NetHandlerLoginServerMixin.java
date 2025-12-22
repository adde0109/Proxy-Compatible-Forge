package org.adde0109.pcf.mixin.v7_10.forge.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.DIRECT_CONNECT_ERR;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.VelocityProxy.PLAYER_INFO_PAYLOAD;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.server.network.NetHandlerLoginServer.LoginState;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.NameAndId;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.forwarding.network.ClientboundCustomQueryPacket;
import org.adde0109.pcf.mixin.v12_2.forge.forwarding.NetworkManagerAccessor;
import org.adde0109.pcf.v12_2.forge.forwarding.modern.NetHandlerLoginServerBridge;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.S2CCustomQueryPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.ServerLoginQueryListener;
import org.apache.commons.lang3.Validate;
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
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
@Implements(@Interface(iface = ServerLoginQueryListener.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin implements NetHandlerLoginServerBridge {
    // spotless:off
    @Shadow @Final public NetworkManager networkManager;
    @Shadow private GameProfile loginGameProfile;
    @Shadow private LoginState currentLoginState;

    @Unique private int pcf$velocityLoginMessageId = -1;

    @Inject(method = "processLoginStart", cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    private void onHandleHello(CallbackInfo ci) {
        if (PCF.instance().forwarding().enabled()) {
            Validate.validState(this.currentLoginState == LoginState.HELLO, "Unexpected hello packet");
            this.pcf$velocityLoginMessageId = ThreadLocalRandom.current().nextInt();
            this.networkManager.scheduleOutboundPacket(
                new S2CCustomQueryPacket(new ClientboundCustomQueryPacket(
                    this.pcf$velocityLoginMessageId, PLAYER_INFO_PAYLOAD)));
            PCF.logger.debug("Sent Forward Request");
            ci.cancel();
        }
    }
    // spotless:on

    public void pcf$handleCustomQueryPacket(C2SCustomQueryPacket packet) {
        if (PCF.instance().forwarding().enabled()
                && packet.transactionId() == this.pcf$velocityLoginMessageId) {
            if (packet.payload() == null) {
                this.bridge$onDisconnect(DIRECT_CONNECT_ERR());
                return;
            }
            final ByteBuf buf = packet.payload().data();

            final ModernForwarding.Data data = forward(buf, this.networkManager.getRemoteAddress());
            if (data.disconnectMsg() != null) {
                this.bridge$onDisconnect(literal(data.disconnectMsg()));
                return;
            }
            ((NetworkManagerAccessor) this.networkManager).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            this.loginGameProfile = data.profile();
            this.bridge$logger_info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
            this.currentLoginState = LoginState.READY_TO_ACCEPT;
        }
    }
}
