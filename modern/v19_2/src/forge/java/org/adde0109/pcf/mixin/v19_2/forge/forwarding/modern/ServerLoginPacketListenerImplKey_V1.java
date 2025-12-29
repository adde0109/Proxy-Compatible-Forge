package org.adde0109.pcf.mixin.v19_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.ProfilePublicKey;

import org.adde0109.pcf.forwarding.modern.ServerLoginPacketListenerKeyBridge_V1;
import org.adde0109.pcf.forwarding.modern.VelocityProxy;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@AConstraint(mappings = Mappings.SEARGE, version = @Versions(MinecraftVersion.V19))
@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplKey_V1 implements ServerLoginPacketListenerKeyBridge_V1 {
    @Override
    public void bridge$setPlayerProfilePublicKey(
            final @Nullable VelocityProxy.ProfilePublicKeyData publicKeyData) {
        if (publicKeyData == null) {
            ((ServerLoginPacketListenerImplAccessor_V1) this).pcf$setPlayerProfilePublicKey(null);
            return;
        }
        ((ServerLoginPacketListenerImplAccessor_V1) this)
                .pcf$setPlayerProfilePublicKey(new ProfilePublicKey(publicKeyData.toMC()));
    }
}
