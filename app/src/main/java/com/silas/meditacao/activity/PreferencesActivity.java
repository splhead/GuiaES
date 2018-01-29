package com.silas.meditacao.activity;


import android.os.Bundle;

import com.silas.meditacao.fragments.SettingsFragment;


public class PreferencesActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
