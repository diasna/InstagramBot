package com.lightenartes.bot.ig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lightenartes.bot.ig.service.FollowBotReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/18/15.
 */
public class CommonUtil {

    public static final String CLIENT_ID = "936be88d158f4168b97c70532fef1346";
    public static final String CLIENT_SECRET = "a0858ad0ba414a6faf8c8b39edb36b2e";
    public static final String CALLBACK_URL = "lightenartes://auth/instagram";

    public static void setOneTimeAlarm(Context context, int min, int max) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 588, getAlarmIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        /**
         1.5 minutes
         int locationUpdateInterval = 1000 * 30 * 3;
         */
        Random random = new Random();
        int randomTime = random.nextInt((max - min) + 1) + min;
        Log.d("Random", randomTime + " Seconds");
        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + (randomTime * 1000), pendingIntent);
    }

    public static boolean isAlarmSet(Context context) {
        return (PendingIntent.getBroadcast(context, 588, getAlarmIntent(context), PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static Intent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, FollowBotReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(FollowBotReceiver.ACTION_FOLLOW);
        return intent;
    }
}
