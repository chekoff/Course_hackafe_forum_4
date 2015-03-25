package com.chekoff.hackafeforum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Method;

public class MainActivity extends ActionBarActivity {
    String LOG = "Hackafe forum";
    //private static final int RESULT_SETTINGS = 1;
    public String networkStatus;
    boolean sortViewsAcc = false, sortRepliesAcc = false, sortActivityAcc = false;
    MSG msg = new MSG();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkStatus = NetworkStatus.getInstance(this).isOnline();


        //ActionBar actionBar = getSupportActionBar();
        /*getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );*/
        getSupportActionBar().setBackgroundDrawable(new
                ColorDrawable(Color.parseColor("#ffffff")));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.hackafe_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new
                ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar().setTitle("");
        getSupportActionBar();

        if (networkStatus != msg.KEY_CONN_NO) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TopicsFragment())
                        .commit();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (networkStatus == msg.KEY_CONN_NO)
            getMenuInflater().inflate(R.menu.menu_no_connection, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.reload) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);//Start the same Activity
            this.finish(); //finish Activity.
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.sort_views) {

            TopicsFragment topicsFragment = new TopicsFragment();

            if (sortViewsAcc) {
                item.setTitle("Sort by views desc");
                sortViewsAcc = false;
                item.setIcon(R.drawable.ic_acc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?order=views";
            } else {
                item.setTitle("Sort by views acc");
                sortViewsAcc = true;
                item.setIcon(R.drawable.ic_desc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?ascending=true&order=views";
            }


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, topicsFragment)
                    .commit();

            return true;
        }

        if (id == R.id.sort_replies) {

            TopicsFragment topicsFragment = new TopicsFragment();

            if (sortRepliesAcc) {
                item.setTitle("Sort by replies desc");
                sortRepliesAcc = false;
                item.setIcon(R.drawable.ic_acc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?order=posts";
            } else {
                item.setTitle("Sort by replies acs");
                sortRepliesAcc = true;
                item.setIcon(R.drawable.ic_desc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?ascending=true&order=posts";
            }


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, topicsFragment)
                    .commit();

            return true;
        }

        if (id == R.id.sort_latest) {

            TopicsFragment topicsFragment = new TopicsFragment();

            if (sortActivityAcc) {
                item.setTitle("Sort by activity desc");
                sortActivityAcc = false;
                item.setIcon(R.drawable.ic_acc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?order=activity";
            } else {
                item.setTitle("Sort by activity acc");
                sortActivityAcc = true;
                item.setIcon(R.drawable.ic_desc);
                topicsFragment.urlMoreTopicURL = "http://frm.hackafe.org/latest.json/?ascending=true&order=activity";
            }


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, topicsFragment)
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        /*if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e(LOG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }*/


        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (networkStatus != msg.KEY_CONN_NO) {
            if (sortViewsAcc)
                menu.findItem(R.id.sort_views).setTitle("Sort by views desc");
            else
                menu.findItem(R.id.sort_views).setTitle("Sort by views acc");

            if (sortRepliesAcc)
                menu.findItem(R.id.sort_replies).setTitle("Sort by replies desc");
            else
                menu.findItem(R.id.sort_replies).setTitle("Sort by replies acc");

            if (sortActivityAcc)
                menu.findItem(R.id.sort_latest).setTitle("Sort by activity desc");
            else
                menu.findItem(R.id.sort_latest).setTitle("Sort by activity acc");
        }

        return true;
    }
}
