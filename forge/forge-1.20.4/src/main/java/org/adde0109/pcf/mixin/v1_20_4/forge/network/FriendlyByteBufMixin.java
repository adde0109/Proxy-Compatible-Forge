package org.adde0109.pcf.mixin.v1_20_4.forge.network;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;

import net.minecraft.network.FriendlyByteBuf;

import org.adde0109.pcf.common.abstractions.Payload;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V20_2, max = MinecraftVersion.V20_4)
@Mixin(FriendlyByteBuf.class)
@Implements(@Interface(iface = Payload.class, prefix = "payload$", remap = Interface.Remap.NONE))
public abstract class FriendlyByteBufMixin {
    @Shadow
    public abstract int readVarInt();

    //    @Shadow public abstract boolean readBoolean();

    @Shadow
    public abstract String readUtf(int maxLength);

    @Shadow
    public abstract UUID readUUID();

    @Shadow
    public abstract FriendlyByteBuf readBytes(byte[] bytes);

    //    @Shadow public abstract int readableBytes();

    //    @Shadow public abstract int readerIndex();

    @Shadow
    public abstract FriendlyByteBuf getBytes(int index, byte[] bytes);

    public int payload$readVarInt() {
        return this.readVarInt();
    }

    //    public boolean payload$readBoolean() {
    //        return this.readBoolean();
    //    }

    public String payload$readUtf(int maxLength) {
        return this.readUtf(maxLength);
    }

    public UUID payload$readUUID() {
        return this.readUUID();
    }

    public void payload$readBytes(byte[] bytes) {
        this.readBytes(bytes);
    }

    //    public int payload$readableBytes() {
    //        return this.readableBytes();
    //    }

    //    public int payload$readerIndex() {
    //        return this.readerIndex();
    //    }

    public void payload$getBytes(int index, byte[] bytes) {
        this.getBytes(index, bytes);
    }
}
