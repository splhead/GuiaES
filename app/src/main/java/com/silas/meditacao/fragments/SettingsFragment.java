package com.silas.meditacao.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.silas.guiaes.activity.R;

/**
 * Created by splhead on 18/01/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
