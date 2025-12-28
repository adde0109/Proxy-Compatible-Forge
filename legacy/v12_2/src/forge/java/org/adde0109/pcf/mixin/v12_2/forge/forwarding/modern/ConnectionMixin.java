package org.adde0109.pcf.mixin.v12_2.forge.forwarding.modern;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

import org.adde0109.pcf.forwarding.modern.ConnectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        version = @Versions(min = MinecraftVersion.V8, max = MinecraftVersion.V12_2))
@Mixin(NetworkManager.class)
public abstract class ConnectionMixin implements ConnectionBridge {
    // spotless:off
    @Shadow public abstract void shadow$sendPacket(Packet<?> packet);
    // spotless:on

    @Override
    public void bridge$send(Object packet) {
        this.shadow$sendPacket((Packet<?>) packet);
    }
}
