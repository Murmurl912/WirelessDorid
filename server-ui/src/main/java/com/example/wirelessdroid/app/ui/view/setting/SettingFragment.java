package com.example.wirelessdroid.app.ui.view.setting;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.example.wirelessdroid.R;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}