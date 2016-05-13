package com.silas.meditacao.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.silas.guiaes.activity.R;

/**
 * Created by silas on 10/05/16.
 */
public class Preferences {
    public final static String FONT_STYLE = "pref_font_size";
    public final static String DARK_THEME = "pref_night_mode";
    private static Preferences appInstance;
    private final Context context;

    public Preferences(Context context) {
        this.context = context;
        appInstance = this;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return appInstance.open().getBoolean(key, defaultValue);
    }

    protected SharedPreferences open() {
//        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected SharedPreferences.Editor edit() {
        return open().edit();
    }

    public FontStyle getFontStyle() {
        return FontStyle.valueOf(open().getString(FONT_STYLE,
                FontStyle.Normal.name()));
    }

    public void setFontStyle(FontStyle style) {
        edit().putString(FONT_STYLE, style.name()).commit();
    }

    public enum FontStyle {
        Pequena(R.style.FontStyle_Pequena, "Pequena"),
        Normal(R.style.FontStyle_Normal, "Normal"),
        Grande(R.style.FontStyle_Grande, "Grande");

        private int resId;
        private String title;

        FontStyle(int resId, String title) {
            this.resId = resId;
            this.title = title;
        }

        public int getResId() {
            return resId;
        }

        public String getTitle() {
            return title;
        }
    }
}
