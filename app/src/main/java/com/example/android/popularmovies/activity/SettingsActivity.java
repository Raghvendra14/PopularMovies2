package com.example.android.popularmovies.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.SettingsFragment;

/**
 * Created by Raghvendra on 01-08-2016.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.action_settings));

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
