package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import static org.adde0109.pcf.common.Component.literal;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.DIRECT_CONNECT_ERR;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.QUERY_IDS;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.forward;
import static org.adde0109.pcf.forwarding.modern.ModernForwarding.handleHello;

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
import org.adde0109.pcf.forwarding.Mode;
import org.adde0109.pcf.forwarding.modern.ModernForwarding;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.adde0109.pcf.mixin.v12_2.forge.forwarding.ConnectionAccessor;
import org.adde0109.pcf.v12_2.forge.forwarding.modern.NetHandlerLoginServerBridge;
import org.adde0109.pcf.v12_2.forge.forwarding.network.C2SCustomQueryPacket;
import org.adde0109.pcf.v12_2.forge.forwarding.network.ServerLoginQueryListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <a
 * href="https://github.com/PaperMC/Paper-archive/blob/ver/1.19.4/patches/server/0874-Add-Velocity-IP-Forwarding-Support.patch">Adapted
 * from Paper</a>
 */
@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V8, max = MinecraftVersion.V12_2))
@Implements(@Interface(iface = ServerLoginQueryListener.class, prefix = "pcf$"))
@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin
        implements NetHandlerLoginServerBridge, ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final public NetworkManager networkManager;
    @Shadow private GameProfile loginGameProfile;
    @Shadow private LoginState currentLoginState;

    @Inject(method = "processLoginStart*", // * b/c signature differs in 1.8.x
            cancellable = true, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1,
            target = "Lnet/minecraft/server/network/NetHandlerLoginServer;currentLoginState:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;"))
    // spotless:on
    private void onHandleHello(CallbackInfo ci) {
        handleHello(this, ci);
    }

    public void pcf$handleCustomQueryPacket(C2SCustomQueryPacket packet) {
        if (PCF.instance().forwarding().enabled()
                && PCF.instance().forwarding().mode().equals(Mode.MODERN)
                && packet.transactionId() == this.pcf$velocityLoginMessageId()) {
            QUERY_IDS.remove(this.pcf$velocityLoginMessageId());
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
            ((ConnectionAccessor) this.networkManager).pcf$setAddress(data.address());

            final NameAndId nameAndId = new NameAndId(data.profile());

            this.loginGameProfile = data.profile();
            this.bridge$logger_info("UUID of player {} is {}", nameAndId.name(), nameAndId.id());
            this.currentLoginState = LoginState.READY_TO_ACCEPT;
        }
    }
}
