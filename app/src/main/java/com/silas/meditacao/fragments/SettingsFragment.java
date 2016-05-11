package com.silas.meditacao.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;

/**
 * Created by splhead on 18/01/16.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_FONT_SIZE = "pref_font_size";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
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
                    break;
                case "Normal":
                    p.setFontStyle(Preferences.FontStyle.Normal);
                    break;
                case "Grande":
                    p.setFontStyle(Preferences.FontStyle.Grande);
                    break;
            }
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
