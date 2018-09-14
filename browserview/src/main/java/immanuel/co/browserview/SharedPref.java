package immanuel.co.browserview;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref extends Application {
    public static SharedPreferences sharedPfrs = null;
    public static String cameracallback = "";
    public static String file_Namee = "IMMI_BV_PREFS";
    public static String device_token = "DEVICE_TOEKN";
    public static Context appContext;

    public static void saveDeviceToken(String deviceToken) {
        if (sharedPfrs == null) {
            sharedPfrs = appContext.getSharedPreferences(file_Namee, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPfrs.edit();
        editor.putString(device_token, deviceToken);
        editor.commit();
    }

    public static String getDeviceToken() {
        if (sharedPfrs == null) {
            sharedPfrs = appContext.getSharedPreferences(file_Namee, Context.MODE_PRIVATE);
        }
        return sharedPfrs.getString(device_token, "");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        appContext = getApplicationContext();
    }
}