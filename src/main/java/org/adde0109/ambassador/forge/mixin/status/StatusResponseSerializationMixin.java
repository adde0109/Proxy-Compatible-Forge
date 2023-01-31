package org.adde0109.ambassador.forge.mixin.status;

import com.google.gson.JsonObject;
import net.minecraft.network.ServerStatusResponse;
import org.adde0109.ambassador.forge.HandshakeDataTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;


@Mixin(ServerStatusResponse.Serializer.class)
public class StatusResponseSerializationMixin {

    @Inject(method = "serialize", at = @At("RETURN"), cancellable = true)
    private void lastSerialization(CallbackInfoReturnable<JsonObject> cir) {
        JsonObject jsonObject = cir.getReturnValue();
        jsonObject.remove("forgeData");

        if(HandshakeDataTransmitter.storedHandshakeData == null)
          HandshakeDataTransmitter.storedHandshakeData = new HandshakeDataTransmitter.handshakeData();

        HandshakeDataTransmitter.handshakeData data = HandshakeDataTransmitter.storedHandshakeData;
        jsonObject.add("modinfo", HandshakeDataTransmitter.serializeJson(new String(data.parts.get(HandshakeDataTransmitter.partNrToSend-1), StandardCharsets.ISO_8859_1),
                HandshakeDataTransmitter.partNrToSend + "-" + String.valueOf(data.parts.size()) + "-" + String.valueOf(data.totalLength) + "-" + Long.toHexString(data.checksum) + data.packetSplitters));

        HandshakeDataTransmitter.partNrToSend = (HandshakeDataTransmitter.partNrToSend >= data.parts.size()) ? 1 : HandshakeDataTransmitter.partNrToSend + 1;


        cir.setReturnValue(jsonObject);
    }


}
