package org.adde0109.pcf.mixin.v20_4.forge.forwarding.modern;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(mappings = Mappings.SEARGE, version = @Versions(min = MinecraftVersion.V20_2))
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplStartClientVerificationMixin
        implements ServerLoginPacketListenerBridge {
    // spotless:off
    @Shadow abstract void shadow$startClientVerification(GameProfile profile);
    // spotless:on

    @Override
    public void bridge$startClientVerification(final @NonNull GameProfile profile) {
        this.shadow$startClientVerification(profile);
    }
}
