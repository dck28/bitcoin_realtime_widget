package com.streetcred.bitcoinrealtimewidget;

/**
 * Created by denniskong on 10/5/13.
 */

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

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

        findPreference("copy_bitcoin_address").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Bitcoin Address", "13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Address Copied. Thank You!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                return true;
            }
        });

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

