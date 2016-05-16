package com.silas.meditacao.activity;

import android.os.Bundle;

import com.silas.meditacao.fragments.SettingsFragment;

/**
 * Created by splhead on 18/01/16.
 */
public class PreferencesActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
