package com.silas.meditacao.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.silas.guiaes.activity.R;

public class Preferences {
    public final static String DARK_THEME = "pref_night_mode";
    public final static String TYPE_DEFAULT = "pref_type_default";
    private final static String FONT_STYLE = "pref_font_size";
    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    private SharedPreferences open() {
//        return favoritesActivity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor edit() {
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
        Pequena(R.style.FontStyle_Pequena),
        Normal(R.style.FontStyle_Normal),
        Grande(R.style.FontStyle_Grande);

        private int resId;

        FontStyle(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }
}
