package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.ProfilePublicKeyData;
import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerBridge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(mappings = Mappings.SEARGE, version = @Versions(MinecraftVersion.V19))
@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplKeyV1Mixin
        implements ServerLoginPacketListenerBridge.KeyV1 {
    // spotless:off
    @SuppressWarnings("MixinAnnotationTarget")
    @Shadow(remap = false)
    private ProfilePublicKey f_215255_; // playerProfilePublicKey
    // spotless:on

    @Override
    public void bridge$setPlayerProfilePublicKey(
            final @Nullable ProfilePublicKeyData publicKeyData) {
        if (publicKeyData == null) {
            this.f_215255_ = null;
            return;
        }
        this.f_215255_ = new ProfilePublicKey(publicKeyData.toMC());
    }
}
