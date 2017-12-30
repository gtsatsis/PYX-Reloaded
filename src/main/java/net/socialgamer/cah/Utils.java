package net.socialgamer.cah;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;

import java.util.Set;

public class Utils {

    public static JsonArray toJsonArray(Set<Integer> set) {
        JsonArray jsonArray = new JsonArray(set.size());
        for (int item : set) jsonArray.add(item);
        return jsonArray;
    }

    public static JsonObject singletonJsonObject(String key, JsonElement element) {
        JsonObject json = new JsonObject();
        json.add(key, element);
        return json;
    }

    public static JsonObject singletonJsonObject(String key, String str) {
        JsonObject json = new JsonObject();
        json.addProperty(key, str);
        return json;
    }

    public static JsonObject singletonJsonObject(String key, Number num) {
        JsonObject json = new JsonObject();
        json.addProperty(key, num);
        return json;
    }

    public static JsonArray singletonJsonArray(JsonElement element) {
        JsonArray json = new JsonArray(1);
        json.add(element);
        return json;
    }

    public static NanoHTTPD.Response methodNotAllowed() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, null, null);
    }

    public static String optString(JsonObject obj, String key, String fallback) {
        JsonElement element = obj.get(key);
        if (element == null) return fallback;
        else return element.getAsString();
    }

    public static int optInt(JsonObject obj, String key, int fallback) {
        JsonElement element = obj.get(key);
        if (element == null) return fallback;
        else return element.getAsInt();
    }
}
