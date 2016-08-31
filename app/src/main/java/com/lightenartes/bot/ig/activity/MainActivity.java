package com.lightenartes.bot.ig.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lightenartes.bot.ig.CommonUtil;
import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.activity.fragment.FragmentDrawer;
import com.lightenartes.bot.ig.activity.fragment.HomeFragment;
import com.lightenartes.bot.ig.db.Souls;
import com.lightenartes.bot.ig.db.Users;
import com.lightenartes.bot.ig.service.FollowBotService;
import com.orm.query.Select;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    HomeFragment fragment = null;

    Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(new FragmentDrawer.FragmentDrawerListener() {
            @Override
            public void onDrawerItemSelected(Users users) {
                displayView(users);
            }
        });

        getSupportActionBar().setTitle(R.string.app_name);

        Users users = Select.from(Users.class).limit("1").first();
        if (users != null) {
            displayView(users);
        }

        if (!CommonUtil.isAlarmSet(this)) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            int min = Integer.parseInt(pref.getString("random_interval_min", "60"));
            int max = Integer.parseInt(pref.getString("random_interval_max", "80"));

            CommonUtil.setOneTimeAlarm(this, min, max);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, BotPreferenceActivity.class));
            return true;
        }

        if (id == R.id.action_delete) {
            Users.deleteAll(Users.class, "id = ?", currentUser.getId() + "");
            Souls.deleteAll(Souls.class, "users = ?", currentUser.getId() + "");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayView(Users users) {
        this.currentUser = users;
        if (fragment == null) {
            fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        } else {
            getSupportActionBar().setTitle("@" + users.getUsername());
            fragment.loadData(users);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayView(currentUser);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(FollowBotService.DONE_EVENT));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayView(currentUser);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}