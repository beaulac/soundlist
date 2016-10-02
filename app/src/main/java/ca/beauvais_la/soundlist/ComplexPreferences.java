package ca.beauvais_la.soundlist;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author alacasse (10/1/16)
 */
public class ComplexPreferences {

    private static ComplexPreferences complexPreferences;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ComplexPreferences(Context context, String namePreferences, int mode) {
        this.context = context;
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "complex_preferences";
        }
        preferences = context.getSharedPreferences(namePreferences, mode);
        editor = preferences.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context context,
                                                           String namePreferences, int mode) {

        if (complexPreferences == null) {
            complexPreferences = new ComplexPreferences(context,
                                                        namePreferences, mode);
        }

        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, JsonUtil.serialize(object));
    }

    public void commit() {
        editor.commit();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return JsonUtil.deserializeAs(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");
            }
        }
    }

    public void clearObject() {
        editor.clear();
    }
}