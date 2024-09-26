package org.adde0109.pcf.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CommonInitializer {
    public static ModernForwarding modernForwardingInstance;

    public static final int QUERY_ID = 100;
    public static final String velocityChannel = "velocity:player_info";
    public static Function<String, Object> resourceLocation = null;

    public static Object channelResource() {
        return resourceLocation.apply(velocityChannel);
    }

    public static final List<String> integratedArgumentTypes = new ArrayList<>();

    public static void setupIntegratedArgumentTypes() {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(CommonInitializer.class
                .getResourceAsStream("/integrated_argument_types.json")))) {
            JsonObject result = new Gson().fromJson(reader, JsonObject.class);
            result.get("entries").getAsJsonArray().iterator().forEachRemaining((k) -> integratedArgumentTypes.add(k.getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
