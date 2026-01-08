package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(mappings = Mappings.SEARGE, version = @Versions(max = MinecraftVersion.V20_1))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplStartClientVerificationMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow @Nullable public GameProfile gameProfile;
    @Shadow ServerLoginPacketListenerImpl.State state;
    // spotless:on

    @Override
    public void bridge$startClientVerification(final @NonNull GameProfile profile) {
        this.gameProfile = profile;
        this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
    }
}
