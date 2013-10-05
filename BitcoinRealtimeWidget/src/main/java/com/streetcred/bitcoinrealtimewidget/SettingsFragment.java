package com.streetcred.bitcoinrealtimewidget;

/**
 * Created by denniskong on 10/5/13.
 */

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    public static final String FRAGMENT_TAG = getFragmentTag(SettingsFragment.class);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);

        UpdateFrequencyDialog listPreference = (UpdateFrequencyDialog) findPreference("pref_freq");
        if (listPreference != null){
            if (listPreference.getValue() == null) {
                listPreference.setValueIndex(0);
            }
            listPreference.setSummary(Constants.PREF_FREQ_SUMMARY + listPreference.getValue() + ".");

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    preference.setSummary(Constants.PREF_FREQ_SUMMARY + newValue.toString() + ".");
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public static String getFragmentTag (Class <? extends Fragment> fragmentClass){
        return fragmentClass.getSimpleName();
    }

}

