package com.silas.meditacao.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.silas.guiaes.activity.R;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.io.TimePreference;
import com.silas.meditacao.io.TimePreferenceDialogFragmentCompat;
import com.silas.meditacao.io.Util;
import com.silas.meditacao.receiver.SchedulerReceiver;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_FONT_SIZE = "pref_font_size";
    public final static String KEY_DARK_THEME = "pref_night_mode";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        if (getActivity() != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
        }

        // If it was one of our cutom Preferences, show its dialog
        if (dialogFragment != null) {
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_FONT_SIZE:
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

                break;
            case KEY_DARK_THEME:
                mFirebaseAnalytics.setUserProperty("theme", "darktheme");
                Util.restart(getActivity());
                break;
            case TimePreference.TIMEPREFERENCE_KEY:
                Context ctx = getActivity();
                if (ctx != null) {
                    mFirebaseAnalytics.setUserProperty("notification_time", "changed");
                    SchedulerReceiver.setAlarm(ctx);
                }
                break;
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
