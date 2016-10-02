package ca.beauvais_la.soundlist;

import android.content.Context;
import ca.beauvais_la.soundlist.model.FBUser;

/**
 * @author alacasse (10/1/16)
 */
public class PrefUtils {

    public static void setCurrentUser(FBUser currentUser, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public static FBUser getCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        FBUser currentUser = complexPreferences.getObject("current_user_value", FBUser.class);
        return currentUser;
    }

    public static void clearCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

}
