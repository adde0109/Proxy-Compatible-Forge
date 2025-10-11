package org.adde0109.pcf.mixin.v1_20_2.neoforge.login;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
import org.adde0109.pcf.common.reflection.StateUtil;
import org.adde0109.pcf.v1_20_2.neoforge.Compatibility;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap.DIRECT_CONN_ERR;
import static org.adde0109.pcf.v1_20_2.neoforge.forwarding.FWDBootstrap.PLAYER_INFO_CHANNEL;

@ReqMappings(Mappings.MOJANG)
@ReqMCVersion(min = MinecraftVersion.V20_2)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ModernForwardingMixin {
    @Shadow @Final net.minecraft.network.Connection connection;

    @Shadow @Nullable public GameProfile authenticatedProfile;

    @Shadow
    public abstract void shadow$disconnect(Component reason);

    @Unique private boolean pcf$listen = false;

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (PCF.modernForwarding != null) {
            StateUtil.setState(this, 0);
            PCF.logger.debug("Sent Forward Request");
            this.connection.send(new ClientboundCustomQueryPacket(PCF.QUERY_ID, new DiscardedQueryPayload(PLAYER_INFO_CHANNEL)));
            this.pcf$listen = true;
            ci.cancel();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(
            ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        if ((packet.transactionId() == PCF.QUERY_ID)
                && StateUtil.stateEquals(this, 0)
                && this.pcf$listen) {
            this.pcf$listen = false;
            try {
                if (packet.payload() == null) {
                    throw new Exception("Got empty packet");
                }
                FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
                packet.payload().write(data);

                Compatibility.neoForgeReadSimpleQueryPayload(data);
                Compatibility.applyFFAPIFix(this);

                this.authenticatedProfile =
                        PCF.modernForwarding.handleForwardingPacket(
                                (Payload) data, (Connection) connection);
                this.arclight$preLogin();
                StateUtil.setState(this, 4);
            } catch (Exception e) {
                this.shadow$disconnect(DIRECT_CONN_ERR);
                PCF.logger.warn("Exception verifying forwarded player info", e);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
