package org.adde0109.ambassador.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.network.ConfigSync;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HandshakeDataTransmitter {

  public static int partNrToSend;

  @Nullable
  public static handshakeData storedHandshakeData;

  private static final int MAX_DATA_LENGTH = 16000;


  public HandshakeDataTransmitter() {
    partNrToSend = 1;
  }

  public static class handshakeData {
    public String packetSplitters;
    public List<byte[]> parts;
    public int totalLength;
    public handshakeData() {
      packetSplitters = "";
      parts = new ArrayList<>();
      buildData();
    }

    private void buildData() {
      HandshakeMessages.S2CModList s2CModList = new HandshakeMessages.S2CModList();
      List<Pair<String, HandshakeMessages.S2CRegistry>> registryPackets = RegistryManager.generateRegistryPackets(false);
      List<Pair<String, HandshakeMessages.S2CConfigData>> configPackets = ConfigSync.INSTANCE.syncConfigs(false);


      FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());


      //Mod List
      packetSplitters += ":" + Integer.toString(buffer.writerIndex());

      buffer.writeResourceLocation(new ResourceLocation("fml:handshake"));

      //int index = buffer.writerIndex();
      //LogManager.getLogger().warn("Length is at index: " + String.valueOf(index));
      buffer.writeVarInt(calculateLength(s2CModList));
      buffer.writeVarInt(1);
      s2CModList.encode(buffer);


      //Registries
      for (Pair<String, HandshakeMessages.S2CRegistry> registryPacket : registryPackets) {
        packetSplitters += ":" + Integer.toString(buffer.writerIndex());

        HandshakeMessages.S2CRegistry registry = registryPacket.getRight();

        buffer.writeResourceLocation(new ResourceLocation("fml:handshake"));
        buffer.writeVarInt(calculateLength(registry));
        buffer.writeVarInt(3);
        encode(registry, buffer);
      }

      //Configs
      for (Pair<String, HandshakeMessages.S2CConfigData> configPacket : configPackets) {
        packetSplitters += ":" + Integer.toString(buffer.writerIndex());

        HandshakeMessages.S2CConfigData config = configPacket.getRight();

        buffer.writeResourceLocation(new ResourceLocation("fml:handshake"));
        buffer.writeVarInt(calculateLength(config));
        buffer.writeVarInt(4);
        encode(config, buffer);
      }



      //Place everything into an array
      //Splice into parts to fit a statusResponse
      totalLength = buffer.readableBytes();
      while (buffer.readableBytes() > 0) {
        byte[] data = new byte[Math.min(buffer.readableBytes(), MAX_DATA_LENGTH)];
        buffer.readBytes(data);
        parts.add(data);
      }
      buffer.release();
    }
  }









  private static int calculateLength(HandshakeMessages.S2CModList s2CModList) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    buffer.writeVarInt(1);
    s2CModList.encode(buffer);
    int length = buffer.writerIndex();
    buffer.release();
    return length;
  }

  private static int calculateLength(HandshakeMessages.S2CRegistry s2CRegistry) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    buffer.writeVarInt(2);
    encode(s2CRegistry,buffer);
    int length = buffer.writerIndex();
    buffer.release();
    return length;
  }

  private static int calculateLength(HandshakeMessages.S2CConfigData s2CConfigData) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    buffer.writeVarInt(2);
    encode(s2CConfigData,buffer);
    int length = buffer.writerIndex();
    buffer.release();
    return length;
  }

  private static void encode(HandshakeMessages.S2CRegistry config, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(config.getRegistryName());
    buffer.writeBoolean(config.hasSnapshot());
    if (config.hasSnapshot())
      buffer.writeBytes(config.getSnapshot().getPacketData());
  }

  private static void encode(HandshakeMessages.S2CConfigData config, FriendlyByteBuf buffer) {
    buffer.writeUtf(config.getFileName());
    buffer.writeByteArray(config.getBytes());
  }


  private static int calculateAvailableSize(JsonObject jsonObject) {
    jsonObject.add("modinfo",serializeJson("0","1-20:0:32325:64574:99879:---------------------"));
    int size = ClientboundStatusResponsePacket.GSON.toJson(jsonObject).length();
    jsonObject.remove("modinfo");
    return 32767-size;
  }


  public static JsonObject serializeJson(String data,String version) {
    JsonObject modinfo = new JsonObject();
    JsonArray modList = new JsonArray();

    JsonObject mod = new JsonObject();

    mod.addProperty("modid",data);
    mod.addProperty("version", version);

    modList.add(mod);

    modinfo.addProperty("type", "ambassador");
    modinfo.add("modList",modList);

    return modinfo;

  }

}


