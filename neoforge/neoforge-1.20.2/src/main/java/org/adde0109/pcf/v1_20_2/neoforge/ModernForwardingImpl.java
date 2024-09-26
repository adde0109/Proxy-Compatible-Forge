package org.adde0109.pcf.v1_20_2.neoforge;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.MinecraftVersion;
import dev.neuralnexus.taterapi.Platform;

import io.netty.buffer.Unpooled;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.adde0109.pcf.common.ModernForwarding;
import org.adde0109.pcf.common.abstractions.Connection;
import org.adde0109.pcf.common.abstractions.Payload;
import org.jetbrains.annotations.Nullable;

public class ModernForwardingImpl extends ModernForwarding {
    ModernForwardingImpl(String forwardingSecret) {
        super(forwardingSecret);
    }

    @Nullable
    public GameProfile handleForwardingPacket(ServerboundCustomQueryAnswerPacket packet, net.minecraft.network.Connection connection) throws Exception {
        if(packet.payload() == null) {
            throw new Exception("Got empty packet");
        }
        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        packet.payload().write(data);

        // NeoForge 1.20.2 start - Work around NeoForge's SimpleQueryPayload
        if (Platform.get().is(Platform.NEOFORGE) && MinecraftVersion.get().is(MinecraftVersion.V1_20_2)) {
            data.readVarInt();
            data.readResourceLocation();
        }
        // NeoForge 1.20.2 end - Work around NeoForge's SimpleQueryPayload

        return this.handleForwardingPacket((Payload) data, (Connection) connection);
    }
}
