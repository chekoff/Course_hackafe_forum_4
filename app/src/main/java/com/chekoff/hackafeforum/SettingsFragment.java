package com.chekoff.hackafeforum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.io.File;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final Preference clearCache = (Preference) findPreference("clear_cache");
        clearCache.setSummary("Cache size " + getCacheSize());

        clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dlg_cache_title)
                        .setMessage(R.string.dlg_cache_message)
                        .setPositiveButton(R.string.dlg_yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearImageCache();

                                clearCache.setSummary("Cache size " + getCacheSize());
                            }

                        })
                        .setNegativeButton(R.string.dlg_no, null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //if (key.equals(SettingsActivity.KEY_LOAD_POST_IMAGES))
        TopicsFragment.isSettingsChanged = true;

    }

    private String getCacheSize() {
        double size = 0;
        String dimension = "";

        File cacheDir = new File(getActivity().getCacheDir(), "avatars");

        File[] files = cacheDir.listFiles();
        for (File f : files) {
            size = size + f.length();
        }

        size = Math.round(size / 1024 * 100.0) / 100.0;
        dimension = " KB";

        if (size > 1024) {
            size = Math.round(size / 1024 * 100.0) / 100.0;
            dimension = " MB";
        }

        if (size > 1024) {
            size = Math.round(size / 1024 * 100.0) / 100.0;
            dimension = " GB";
        }

        return size + dimension;
    }

    private void clearImageCache() {
        File cacheDir = new File(getActivity().getCacheDir(), "avatars");

        File[] files = cacheDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }


}
