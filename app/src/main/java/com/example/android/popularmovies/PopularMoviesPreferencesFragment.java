package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Ben on 15/04/2016.
 */
public class PopularMoviesPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
