package org.adde0109.pcf.mixin.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.adde0109.pcf.Initializer;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerLoginPacketListenerImpl.class)
public class ModernForwardingMixin {

    @Shadow
    private Connection connection;

    @Shadow
    private GameProfile authenticatedProfile;

    @Shadow
    private void disconnect(Component p_194026_1_) {
    }

    @Shadow
    private ServerLoginPacketListenerImpl.State state;

    private static final ResourceLocation VELOCITY_RESOURCE = new ResourceLocation("velocity:player_info");
    private boolean ambassador$listen = false;

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(CallbackInfo ci) {
        Validate.validState(state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet");
        if (Initializer.modernForwardingInstance != null) {
            this.state = ServerLoginPacketListenerImpl.State.HELLO;
            LogManager.getLogger().debug("Sent Forward Request");
            connection.send(new ClientboundCustomQueryPacket(100, new DiscardedQueryPayload(VELOCITY_RESOURCE)));
            ambassador$listen = true;
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryAnswerPacket packet, CallbackInfo ci) {
        if ((packet.transactionId() == 100) && state == ServerLoginPacketListenerImpl.State.HELLO && ambassador$listen) {
            ambassador$listen = false;
            try {
                this.authenticatedProfile = Initializer.modernForwardingInstance.handleForwardingPacket(packet, connection);
                arclight$preLogin();
                this.state = ServerLoginPacketListenerImpl.State.VERIFYING;
            } catch (Exception e) {
                this.disconnect(Component.literal("Direct connections to this server are not permitted!"));
                LogManager.getLogger().warn("Exception verifying forwarded player info", e);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    void arclight$preLogin() throws Exception {}

}
