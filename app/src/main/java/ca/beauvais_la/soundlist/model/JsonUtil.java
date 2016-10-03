package ca.beauvais_la.soundlist.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author alacasse (10/1/16)
 */
public class JsonUtil {

    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    public static String serialize(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T deserializeAs(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

}
