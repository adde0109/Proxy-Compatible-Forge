package org.adde0109.pcf.mixin.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.adde0109.pcf.Initializer;
import org.adde0109.pcf.StateUtil;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ModernForwardingMixin {
    @Shadow @Final Connection connection;

    @Shadow @Nullable public GameProfile gameProfile;

    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private static final ResourceLocation pcf$VELOCITY_RESOURCE = new ResourceLocation("velocity:player_info");

    @Unique private boolean pcf$listen = false;

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (Initializer.modernForwardingInstance != null) {
            StateUtil.setState(this, 0);
            LogManager.getLogger().debug("Sent Forward Request");
            this.connection.send(new ClientboundCustomQueryPacket(100, pcf$VELOCITY_RESOURCE, new FriendlyByteBuf(Unpooled.EMPTY_BUFFER)));
            this.pcf$listen = true;
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if ((packet.getTransactionId() == CommonInitializer.QUERY_ID) && StateUtil.stateEquals(this, 0) && this.pcf$listen) {
            this.pcf$listen = false;
            try {
                this.gameProfile = Initializer.modernForwardingInstance.handleForwardingPacket(packet, connection);
                this.arclight$preLogin();
                StateUtil.setState(this, 3);
            } catch (Exception e) {
                this.shadow$disconnect(Component.nullToEmpty("Direct connections to this server are not permitted!"));
                LogManager.getLogger().warn("Exception verifying forwarded player info", e);
            }
            ci.cancel();
        }
    }

    @Shadow(remap = false)
    @SuppressWarnings({"MixinAnnotationTarget", "RedundantThrows"})
    void arclight$preLogin() throws Exception {}
}
