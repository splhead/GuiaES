package com.silas.meditacao.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;

/**
 * Created by silas on 13/05/16.
 */
public abstract class ThemedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Preferences.DARK_THEME, false)) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Light);
        }
        Preferences preferences = new Preferences(this);
        getTheme().applyStyle(preferences.getFontStyle().getResId(), true);
        super.onCreate(savedInstanceState);
    }
}
