package org.adde0109.pcf.v7_10.forge;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.network.NetworkAdapters;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.S2CCustomQueryPacket;
import org.jetbrains.annotations.NotNull;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V7, max = MinecraftVersion.V7_10))
public final class AdapterRegistryInit implements PCFInitializer {
    public AdapterRegistryInit() {
        NetworkAdapters.register(
                CCustomQueryPacketAdapter.INSTANCE, SCustomQueryAnswerPacketAdapter.INSTANCE);
    }

    @Override
    public void onInit() {}

    public static final class CCustomQueryPacketAdapter
            implements AdapterCodec<S2CCustomQueryPacket, ClientboundCustomQueryPacket> {
        public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

        @Override
        public @NotNull ClientboundCustomQueryPacket from(
                final @NotNull S2CCustomQueryPacket object) {
            return new ClientboundCustomQueryPacket(object.transactionId(), object.payload());
        }

        @Override
        public @NotNull S2CCustomQueryPacket to(
                final @NotNull ClientboundCustomQueryPacket object) {
            return new S2CCustomQueryPacket(object);
        }
    }

    public static final class SCustomQueryAnswerPacketAdapter
            implements AdapterCodec<
                    C2SCustomQueryAnswerPacket, ServerboundCustomQueryAnswerPacket> {
        public static final SCustomQueryAnswerPacketAdapter INSTANCE =
                new SCustomQueryAnswerPacketAdapter();

        @Override
        public @NotNull ServerboundCustomQueryAnswerPacket from(
                final @NotNull C2SCustomQueryAnswerPacket object) {
            if (object.payload() == null) {
                return new ServerboundCustomQueryAnswerPacket(object.transactionId());
            }
            return new ServerboundCustomQueryAnswerPacket(object.transactionId(), object.payload());
        }

        @Override
        public @NotNull C2SCustomQueryAnswerPacket to(
                final @NotNull ServerboundCustomQueryAnswerPacket object) {
            return new C2SCustomQueryAnswerPacket(object);
        }
    }
}
