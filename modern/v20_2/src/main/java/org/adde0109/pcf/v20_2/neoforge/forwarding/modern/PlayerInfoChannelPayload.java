package org.adde0109.pcf.v20_2.neoforge.forwarding.modern;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Payload for the player info channel <br>
 * Adapted from <a
 * href="https://github.com/PaperMC/Paper/blob/main/paper-server/patches/sources/net/minecraft/network/protocol/login/ClientboundCustomQueryPacket.java.patch#L8-L15">PaperMC</a>
 *
 * @param id the resource location id
 * @param buffer the buffer
 */
public record PlayerInfoChannelPayload(ResourceLocation id, ByteBuf buffer)
        implements CustomQueryPayload {
    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBytes(this.buffer.copy());
    }
}
