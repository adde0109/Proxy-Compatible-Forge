package org.adde0109.pcf;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Mod("pcf")
public class Initializer {

  public static ModernForwarding modernForwardingInstance;
  public static final List<String> integratedArgumentTypes = new ArrayList<>();

  public static final Config config;

  public Initializer() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,configSpec);

    //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

    MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
    try (Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass()
            .getResourceAsStream("/integrated_argument_types.json")))) {
      JsonObject result = new Gson().fromJson(reader, JsonObject.class);
      result.get("entries").getAsJsonArray().iterator().forEachRemaining((k) -> integratedArgumentTypes.add(k.getAsString()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void serverAboutToStart(FMLServerAboutToStartEvent event) {
    String forwardingSecret = config.forwardingSecret.get();
    if(!forwardingSecret.isEmpty()) { // forwardingSecret.isBlank() is Java 11+
      modernForwardingInstance = new ModernForwarding(forwardingSecret);
    }
  }


  static final ForgeConfigSpec configSpec;
  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
    configSpec = specPair.getRight();
    config = specPair.getLeft();
  }

  public static class Config {
    public final ForgeConfigSpec.ConfigValue<? extends String> forwardingSecret;

    Config(ForgeConfigSpec.Builder builder)
    {
      builder.comment("Modern Forwarding Settings")
              .push("modernForwarding");

      forwardingSecret = builder
              .define("forwardingSecret", "");

      builder.pop();
    }

  }
}
