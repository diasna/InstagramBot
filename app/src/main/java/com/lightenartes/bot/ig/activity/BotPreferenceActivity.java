package com.lightenartes.bot.ig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.lightenartes.bot.ig.CommonUtil;
import com.lightenartes.bot.ig.R;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/26/15.
 */
public class BotPreferenceActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences pref, String s) {
                CommonUtil.setOneTimeAlarm(BotPreferenceActivity.this,
                        Integer.parseInt(pref.getString("random_interval_min", "60")),
                        Integer.parseInt(pref.getString("random_interval_max", "80"))
                );
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        bar.setTitle("Settings");
        bar.setTitleTextColor(Color.WHITE);
        root.addView(bar, 0);
    }

}
