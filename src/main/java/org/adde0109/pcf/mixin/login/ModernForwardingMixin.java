package org.adde0109.pcf.mixin.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.network.NetworkDirection;
import org.adde0109.pcf.Initializer;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = ServerLoginPacketListenerImpl.class, priority = 300)
public class ModernForwardingMixin {

    @Shadow
    private Connection connection;

    @Shadow
    private GameProfile gameProfile;

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
            this.connection.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(new FriendlyByteBuf(Unpooled.EMPTY_BUFFER), 100), VELOCITY_RESOURCE).getThis());
            ambassador$listen = true;
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket p_209526_1_, CallbackInfo ci) {
        if ((Initializer.modernForwardingInstance != null) && (p_209526_1_.getIndex() == 100) && ambassador$listen) {
            this.gameProfile = Initializer.modernForwardingInstance.handleForwardingPacket(p_209526_1_);
            ambassador$listen = false;
            if (this.gameProfile == null) {
                this.disconnect(Component.literal("Direct connections to this server are not permitted!"));
                LogManager.getLogger().error("Attention! Someone tried to join directly!");
            } else {
                arclight$preLogin();
                this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
            }
            ci.cancel();
        }
    }

    void arclight$preLogin() {
    }

}
