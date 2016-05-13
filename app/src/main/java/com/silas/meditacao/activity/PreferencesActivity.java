package com.silas.meditacao.activity;

import android.os.Bundle;

import com.silas.meditacao.fragments.SettingsFragment;
import com.silas.meditacao.io.Preferences;

/**
 * Created by splhead on 18/01/16.
 */
public class PreferencesActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(new Preferences(this).getFontStyle().getResId(), true);
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
