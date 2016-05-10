package com.silas.meditacao.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.silas.guiaes.activity.R;

/**
 * Created by silas on 10/05/16.
 */
public class Preferences {
    private final static String FONT_STYLE = "pref_font_size";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
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
                FontStyle.Medium.name()));
    }

    public void setFontStyle(FontStyle style) {
        edit().putString(FONT_STYLE, style.name()).commit();
    }

    public enum FontStyle {
        Small(R.style.FontStyle_Small, "Small"),
        Medium(R.style.FontStyle_Medium, "Medium"),
        Large(R.style.FontStyle_Large, "Large");

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
