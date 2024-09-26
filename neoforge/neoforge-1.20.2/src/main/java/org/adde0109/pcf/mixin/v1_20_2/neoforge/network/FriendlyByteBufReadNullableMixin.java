package org.adde0109.pcf.mixin.v1_20_2.neoforge.network;

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.common.abstractions.Payload;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@ReqMappings(Mappings.MOJMAP)
@ReqMCVersion(min = MinecraftVersion.V1_20_2, max = MinecraftVersion.V1_20_4)
@Mixin(FriendlyByteBuf.class)
@Implements(@Interface(iface = Payload.class, prefix = "payload$", remap = Interface.Remap.NONE))
public abstract class FriendlyByteBufReadNullableMixin {
    @Shadow public abstract <T> T readNullable(FriendlyByteBuf.Reader<T> par1);

    public Payload payload$readNullable(Function<Payload, Payload> function) {
        return this.readNullable((buf) -> function.apply((Payload) buf));
    }
}
