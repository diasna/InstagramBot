package com.lightenartes.bot.ig.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lightenartes.bot.ig.CommonUtil;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/18/15.
 */
public class FollowBotReceiver extends android.content.BroadcastReceiver {

    public static final String ACTION_FOLLOW = "com.lightenartes.bot.FollowBot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            int min = Integer.parseInt(pref.getString("random_interval_min", "60"));
            int max = Integer.parseInt(pref.getString("random_interval_max", "80"));
            CommonUtil.setOneTimeAlarm(context, min, max);
        } else if(ACTION_FOLLOW.equals(intent.getAction())) {
            Log.d("FollowBotReceiver", "Alarm Follow Received");
            Intent followBot = new Intent(context, FollowBotServiceSync.class);
            context.startService(followBot);
        }
    }
}
