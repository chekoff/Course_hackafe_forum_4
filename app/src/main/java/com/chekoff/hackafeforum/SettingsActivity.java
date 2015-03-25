package com.chekoff.hackafeforum;

import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Created by Plamen on 19.03.2015.
 */
public class SettingsActivity extends ActionBarActivity {
    public static String KEY_LOAD_POST_IMAGES = "load_posts_images";
    public static String KEY_LOAD_POST_IMAGES_CACHED = "load_posts_images_only_cached";
    public static String KEY_LOAD_POST_IMAGES_NO_CACHED_WIFI = "load_posts_images_only_wifi";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new
                ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_container, new SettingsFragment())
                    .commit();

        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
        }
        return (true);
    }
}
