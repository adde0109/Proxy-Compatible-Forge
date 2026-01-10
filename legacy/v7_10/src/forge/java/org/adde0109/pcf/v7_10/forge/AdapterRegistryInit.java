package org.adde0109.pcf.v7_10.forge;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.network.NetworkAdapters;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.serialization.Result;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import org.adde0109.pcf.PCFInitializer;
import org.adde0109.pcf.v7_10.forge.forwarding.network.C2SCustomQueryAnswerPacket;
import org.adde0109.pcf.v7_10.forge.forwarding.network.S2CCustomQueryPacket;

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
            implements ReversibleCodec<S2CCustomQueryPacket, ClientboundCustomQueryPacket> {
        public static final CCustomQueryPacketAdapter INSTANCE = new CCustomQueryPacketAdapter();

        @Override
        public Result<ClientboundCustomQueryPacket> encode(S2CCustomQueryPacket object) {
            return Result.success(
                    new ClientboundCustomQueryPacket(object.transactionId(), object.payload()));
        }

        @Override
        public Result<S2CCustomQueryPacket> decode(ClientboundCustomQueryPacket object) {
            return Result.success(new S2CCustomQueryPacket(object));
        }
    }

    public static final class SCustomQueryAnswerPacketAdapter
            implements ReversibleCodec<
                    C2SCustomQueryAnswerPacket, ServerboundCustomQueryAnswerPacket> {
        public static final SCustomQueryAnswerPacketAdapter INSTANCE =
                new SCustomQueryAnswerPacketAdapter();

        @Override
        public Result<ServerboundCustomQueryAnswerPacket> encode(
                C2SCustomQueryAnswerPacket object) {
            if (object.payload() == null) {
                return Result.success(
                        new ServerboundCustomQueryAnswerPacket(object.transactionId()));
            }
            return Result.success(
                    new ServerboundCustomQueryAnswerPacket(
                            object.transactionId(), object.payload()));
        }

        @Override
        public Result<C2SCustomQueryAnswerPacket> decode(
                ServerboundCustomQueryAnswerPacket object) {
            return Result.success(new C2SCustomQueryAnswerPacket(object));
        }
    }
}
