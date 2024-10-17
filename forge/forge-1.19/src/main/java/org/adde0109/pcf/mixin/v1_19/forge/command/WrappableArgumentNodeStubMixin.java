package org.adde0109.pcf.mixin.v1_19.forge.command;

/*
The MIT License (MIT)

        Copyright (c) 2020 Andrew Steinborn
        Copyright (c) 2020 Velocity Contributors

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in
        all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
        THE SOFTWARE.
*/

import dev.neuralnexus.conditionalmixins.annotations.ReqMCVersion;
import dev.neuralnexus.conditionalmixins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.Mappings;
import dev.neuralnexus.taterapi.MinecraftVersion;

import io.netty.buffer.Unpooled;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import org.adde0109.pcf.PCF;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ReqMappings(Mappings.SEARGE)
@ReqMCVersion(min = MinecraftVersion.V1_19, max = MinecraftVersion.V1_20_4)
@Mixin(targets = "net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub")
public class WrappableArgumentNodeStubMixin {
    @Unique private static final int MOD_ARGUMENT_INDICATOR = -256;

    @Shadow @Final private ArgumentTypeInfo.Template<?> argumentType;

    @Shadow @Final private String id;

    @Shadow @Final private ResourceLocation suggestionId;

    /**
     * @author Daniel Voort.
     * @reason This is easier than injecting and returning before anything is written. There are
     *     viable alternatives available, but this is just the most straightforward and most
     *     development-time efficient. It is highly unlikely for other mods to try to mixin this
     *     particular function.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Overwrite
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.id);

        var typeInfo = argumentType.type();
        var identifier = PCF.commandArgumentTypeKey(typeInfo);
        var id = PCF.commandArgumentTypeId(typeInfo);

        if (identifier != null && PCF.isIntegratedArgument(identifier.toString())) {
            buffer.writeVarInt(id);
            ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, buffer);
        } else {
            PCF.logger.debug("Wrapping argument node stub with identifier: " + identifier);
            buffer.writeVarInt(MOD_ARGUMENT_INDICATOR);
            buffer.writeVarInt(id);

            FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
            ((ArgumentTypeInfo) typeInfo).serializeToNetwork(argumentType, extraData);

            buffer.writeVarInt(extraData.readableBytes());
            buffer.writeBytes(extraData);

            extraData.release();
        }

        if (suggestionId != null) {
            buffer.writeResourceLocation(suggestionId);
        }
    }
}
