package org.adde0109.ambassador.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.Adler32;

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
    public long checksum;
    public handshakeData() {
      packetSplitters = "";
      parts = new ArrayList<>();
      buildData();
    }

    private void buildData() {
      FMLHandshakeMessages.S2CModList s2CModList = new FMLHandshakeMessages.S2CModList();
      List<Pair<String, FMLHandshakeMessages.S2CRegistry>> registryPackets = RegistryManager.generateRegistryPackets(false);
      List<Pair<String, FMLHandshakeMessages.S2CConfigData>> configPackets = ConfigTracker.INSTANCE.syncConfigs(false);


      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());


      //Mod List
      packetSplitters += ":" + Integer.toString(buffer.writerIndex());
      writePacket(buffer,1,s2CModList::encode);

      //Registries
      for (Pair<String, FMLHandshakeMessages.S2CRegistry> registryPacket : registryPackets) {
        packetSplitters += ":" + Integer.toString(buffer.writerIndex());
        writePacket(buffer,3,(byteBuf -> encode(registryPacket.getRight(),byteBuf)));
      }

      //Configs
      for (Pair<String, FMLHandshakeMessages.S2CConfigData> configPacket : configPackets) {
        packetSplitters += ":" + Integer.toString(buffer.writerIndex());
        writePacket(buffer,4,(byteBuf -> encode(configPacket.getRight(),byteBuf)));
      }

      Adler32 adler32 = new Adler32();
      adler32.update(buffer.nioBuffer());
      checksum = adler32.getValue();

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


 private static void writePacket(PacketBuffer byteBuf,int packetID, Consumer<PacketBuffer> consumer) {
   //packet id and data
   PacketBuffer packetIDAndData = new PacketBuffer(Unpooled.buffer());
   packetIDAndData.writeVarInt(packetID);
   consumer.accept(packetIDAndData);

   byteBuf.writeResourceLocation(new ResourceLocation("fml:handshake"));
   byteBuf.writeVarInt(packetIDAndData.writerIndex());
   byteBuf.writeBytes(packetIDAndData);
   packetIDAndData.release();
 }

  private static void encode(FMLHandshakeMessages.S2CRegistry config, PacketBuffer buffer) {
    buffer.writeResourceLocation(config.getRegistryName());
    buffer.writeBoolean(config.hasSnapshot());
    if (config.hasSnapshot())
      buffer.writeBytes(config.getSnapshot().getPacketData());
  }

  private static void encode(FMLHandshakeMessages.S2CConfigData config, PacketBuffer buffer) {
    buffer.writeUtf(config.getFileName());
    buffer.writeByteArray(config.getBytes());
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


