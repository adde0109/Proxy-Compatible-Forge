package org.adde0109.pcf.mixin.v1_19.forge.login;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.common.CommonInitializer;
import org.adde0109.pcf.common.StateUtil;
import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
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

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V1_19, max = MinecraftVersion.V1_20_1)
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ModernForwardingMixin {
    @Shadow @Final public net.minecraft.network.Connection connection;

    @Shadow @Nullable public GameProfile gameProfile;

    @Shadow public abstract void shadow$disconnect(Component reason);

    @Unique private boolean pcf$listen = false;

    @Inject(method = "handleHello", at = @At("HEAD"), cancellable = true)
    private void onHandleHello(CallbackInfo ci) {
        Validate.validState(StateUtil.stateEquals(this, 0), "Unexpected hello packet");
        if (CommonInitializer.modernForwarding != null) {
            StateUtil.setState(this, 0);
            LogManager.getLogger().debug("Sent Forward Request");
            this.connection.send(new ClientboundCustomQueryPacket(100,
                    (ResourceLocation) CommonInitializer.channelResource(),
                    new FriendlyByteBuf(Unpooled.EMPTY_BUFFER)));
            this.pcf$listen = true;
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    private void onHandleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if ((packet.getTransactionId() == CommonInitializer.QUERY_ID) && StateUtil.stateEquals(this, 0) && this.pcf$listen) {
            this.pcf$listen = false;
            try {
                FriendlyByteBuf data = packet.getData();
                if(data == null) {
                    throw new Exception("Got empty packet");
                }
                this.gameProfile = CommonInitializer.modernForwarding
                        .handleForwardingPacket((Payload) data, (Connection) connection);
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
