package org.adde0109.pcf;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;

import org.adde0109.pcf.common.ModernForwarding;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PCF {
    public static final Logger logger = Logger.create("pcf");

    public static ModernForwarding modernForwarding;

    public static final int QUERY_ID = 1203961429;
    public static final String velocityChannel = "velocity:player_info";
    public static Function<String, Object> resourceLocation;

    public static final String directConnErr =
            "Direct connections to this server are not permitted!";
    public static Function<String, Object> component;

    public static Object directConnErrComponent() {
        return component.apply(directConnErr);
    }

    public static Object channelResource() {
        return resourceLocation.apply(velocityChannel);
    }

    public static Function<Object, Object> COMMAND_ARGUMENT_TYPE_KEY;

    public static Object commandArgumentTypeKey(Object type) {
        return COMMAND_ARGUMENT_TYPE_KEY.apply(type);
    }

    public static Function<Object, Integer> COMMAND_ARGUMENT_TYPE_ID;

    public static int commandArgumentTypeId(Object type) {
        return COMMAND_ARGUMENT_TYPE_ID.apply(type);
    }

    public static final List<String> integratedArgumentTypes = new ArrayList<>();

    public static final List<String> moddedArgumentTypes = new ArrayList<>();

    public static boolean isIntegratedArgument(String identifier) {
        return integratedArgumentTypes.contains(identifier)
                && !moddedArgumentTypes.contains(identifier)
                && !isArgumentEdgeCase(identifier);
    }

    public static boolean isArgumentEdgeCase(String identifier) {
        return MetaAPI.instance().isModLoaded("livingthings")
                && identifier.equals("minecraft:entity");
    }

    public static void setupIntegratedArgumentTypes() {
        try (Reader reader =
                new InputStreamReader(
                        Objects.requireNonNull(
                                PCF.class.getResourceAsStream(
                                        "/integrated_argument_types.json")))) {
            JsonObject result = new Gson().fromJson(reader, JsonObject.class);
            result.get("entries")
                    .getAsJsonArray()
                    .iterator()
                    .forEachRemaining((k) -> integratedArgumentTypes.add(k.getAsString()));
        } catch (IOException e) {
            logger.warn("Exception reading integrated argument types JSON", e);
        }
    }
}
