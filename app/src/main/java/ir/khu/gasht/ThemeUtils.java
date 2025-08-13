package ir.khu.gasht;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ThemeUtils {
    public static final String PREFS_NAME = "appData";
    public static final String KEY_THEME = "selected_theme";

    public static final int THEME_RED = 0;
    public static final int THEME_BLUE = 1;
    public static final int THEME_GREEN = 2;
    public static final int THEME_DEFAULT = 3;

    // اعمال تم در اکتیویتی
    public static void applyTheme(Activity activity) {
        int theme = getSavedTheme(activity);
        switch (theme) {
            case THEME_RED:
                activity.setTheme(R.style.Theme_RedTheme);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.Theme_BlueTheme);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.Theme_GreenTheme);
                break;
            default:
                activity.setTheme(R.style.Theme_Gasht);
        }
    }

    // ذخیره تم
    public static void saveTheme(Context context, int theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, theme).apply();
    }

    // دریافت تم ذخیره شده
    public static int getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, THEME_RED);
    }

    // شروع مجدد برنامه برای اعمال تم
    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }
}
