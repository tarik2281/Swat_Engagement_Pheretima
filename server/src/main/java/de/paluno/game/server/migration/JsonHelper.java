package de.paluno.game.server.migration;

import io.vertx.core.json.JsonObject;

public class JsonHelper {

    private JsonHelper() {
    }

    public static JsonObject flatten(JsonObject jsonObject) {
        var result = new JsonObject();

        flatten(jsonObject, result, "");

        return result;
    }

    private static void flatten(JsonObject jsonObject, JsonObject result, String prefix) {
        for (var entry : jsonObject) {
            var key = entry.getKey();
            var value = entry.getValue();

            if (value instanceof JsonObject objectValue) {
                flatten(objectValue, result, prefix + key + ".");
            } else {
                result.put(prefix + key, value);
            }
        }
    }
}
