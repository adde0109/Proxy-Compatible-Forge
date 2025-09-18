package org.adde0109.pcf.mixin.v1_14_4.forge.login;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.PCF;
import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
import org.adde0109.pcf.v1_14_4.forge.reflection.StateUtil;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ModernForwardingMixin {
    @Shadow @Final public net.minecraft.network.Connection connection;

    @Shadow @Nullable private GameProfile gameProfile;

    @Shadow
    public abstract void shadow$disconnect(Component reason);

    @Unique private boolean pcf$listen = false;

    // spotless:off
    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (PCF.modernForwarding != null) {
            StateUtil.setState(this, 0);
            PCF.logger.debug("Sent Forward Request");
            ClientboundCustomQueryPacket packet = new ClientboundCustomQueryPacket();
            ((ClientboundCustomQueryPacketAccessor) packet).setTransactionId(100);
            ((ClientboundCustomQueryPacketAccessor) packet).setIdentifier((ResourceLocation) PCF.channelResource());
            ((ClientboundCustomQueryPacketAccessor) packet).setData(new FriendlyByteBuf(Unpooled.EMPTY_BUFFER));
            this.connection.send(packet);
            this.pcf$listen = true;
            ci.cancel();
        }
    }
    // spotless:on

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if ((((ServerboundCustomQueryPacketAccessor) packet).getTransactionId() == PCF.QUERY_ID)
                && StateUtil.stateEquals(this, 0)
                && this.pcf$listen) {
            this.pcf$listen = false;
            try {
                FriendlyByteBuf data = ((ServerboundCustomQueryPacketAccessor) packet).getData();
                if (data == null) {
                    throw new Exception("Got empty packet");
                }

                this.gameProfile =
                        PCF.modernForwarding.handleForwardingPacket(
                                (Payload) data, (Connection) connection);
                this.arclight$preLogin();
                StateUtil.setState(this, 3);
            } catch (Exception e) {
                this.shadow$disconnect((Component) PCF.directConnErrComponent());
                PCF.logger.warn("Exception verifying forwarded player info", e);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
