package org.adde0109.pcf.v12_2.forge;

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
import org.adde0109.pcf.v12_2.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v12_2.forge.forwarding.network.S2CCustomQueryPacket;
import org.jspecify.annotations.NonNull;

@AConstraint(
        mappings = Mappings.LEGACY_SEARGE,
        platform = Platform.FORGE,
        version = @Versions(min = MinecraftVersion.V8, max = MinecraftVersion.V12_2))
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
        public @NonNull ClientboundCustomQueryPacket from(
                final @NonNull S2CCustomQueryPacket object) {
            return new ClientboundCustomQueryPacket(object.transactionId(), object.payload());
        }

        @Override
        public @NonNull S2CCustomQueryPacket to(
                final @NonNull ClientboundCustomQueryPacket object) {
            return new S2CCustomQueryPacket(object);
        }
    }

    public static final class SCustomQueryAnswerPacketAdapter
            implements AdapterCodec<
                    C2SCustomQueryAnswerPacket, ServerboundCustomQueryAnswerPacket> {
        public static final SCustomQueryAnswerPacketAdapter INSTANCE =
                new SCustomQueryAnswerPacketAdapter();

        @Override
        public @NonNull ServerboundCustomQueryAnswerPacket from(
                final @NonNull C2SCustomQueryAnswerPacket object) {
            if (object.payload() == null) {
                return new ServerboundCustomQueryAnswerPacket(object.transactionId());
            }
            return new ServerboundCustomQueryAnswerPacket(object.transactionId(), object.payload());
        }

        @Override
        public @NonNull C2SCustomQueryAnswerPacket to(
                final @NonNull ServerboundCustomQueryAnswerPacket object) {
            return new C2SCustomQueryAnswerPacket(object);
        }
    }
}
