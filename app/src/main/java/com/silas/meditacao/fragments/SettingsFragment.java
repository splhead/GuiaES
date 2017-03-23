package com.silas.meditacao.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.Util;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_FONT_SIZE = "pref_font_size";
    public final static String KEY_DARK_THEME = "pref_night_mode";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_FONT_SIZE)) {
            Preferences p = new Preferences(getActivity());
            Preference fontSizePref = findPreference(key);
            String fontSize = sharedPreferences.getString(key, "Normal");
            fontSizePref.setSummary(fontSize);
            switch (fontSize) {
                case "Pequena":
                    p.setFontStyle(Preferences.FontStyle.Pequena);
                    Util.restart(getActivity());
                    break;
                case "Normal":
                    p.setFontStyle(Preferences.FontStyle.Normal);
                    Util.restart(getActivity());
                    break;
                case "Grande":
                    p.setFontStyle(Preferences.FontStyle.Grande);
                    Util.restart(getActivity());
                    break;
            }

            mFirebaseAnalytics.setUserProperty("font_size", fontSize);

        } else if (key.equals(KEY_DARK_THEME)) {
            mFirebaseAnalytics.setUserProperty("theme", "darktheme");
            Util.restart(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
