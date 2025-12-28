package org.adde0109.pcf.mixin.v14_4.forge.forwarding.modern;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V14, max = MinecraftVersion.V16_5))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin_Bridge
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Final public Connection connection;
    @Shadow @Nullable private GameProfile gameProfile;
    @Shadow private ServerLoginPacketListenerImpl.State state;
    @Shadow public abstract void shadow$onDisconnect(Component reason);
    @Unique private int pcf$velocityLoginMessageId = -1;
    // spotless:on

    @Override
    public int bridge$velocityLoginMessageId() {
        return this.pcf$velocityLoginMessageId;
    }

    @Override
    public void bridge$setVelocityLoginMessageId(final int id) {
        this.pcf$velocityLoginMessageId = id;
    }

    @Override
    public @NotNull ConnectionBridge bridge$connection() {
        return (ConnectionBridge) this.connection;
    }

    @Override
    public void bridge$disconnect(final @NotNull Object reason) {
        this.shadow$onDisconnect((Component) reason);
    }

    @Override
    public void bridge$startClientVerification(final @NotNull GameProfile profile) {
        this.gameProfile = profile;
        this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
    }
}
