package com.example.chethan.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class AlarmBroadcastReceiver extends BroadcastReceiver {


    @Override
    @DebugLog
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null){
            Timber.d("Intent in onReceive of Alarm BR");
            Timber.d("Hour - "+intent.getExtras().getInt(Constants.END_TIME_HOUR));
            Timber.d("Min - "+intent.getExtras().getInt(Constants.END_TIME_MINUTE));
            Timber.d("Alarm Interval - "+intent.getExtras().getInt(Constants.ALARM_REPEAT_INTERVAL));

            TimeSpeakerBroadcastReceiver timeSpeakerBroadcastReceiver =new TimeSpeakerBroadcastReceiver();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,(intent.getExtras().getInt(Constants.END_TIME_HOUR)));
            calendar.set(Calendar.MINUTE,(intent.getExtras().getInt(Constants.END_TIME_MINUTE)));
            timeSpeakerBroadcastReceiver.SetAlarm(context,intent.getExtras().getInt(Constants.ALARM_REPEAT_INTERVAL),calendar);
        }
        //else dont do anything :-)
    }

    @DebugLog
    public void setAlarm(Context context, long interval,int repeatInterval, int requestCode, Calendar startTime, Calendar endTime) {
        Timber.d("Setting Alarm");
        Timber.d("End time :: "+endTime.get(Calendar.HOUR_OF_DAY) + " : "+endTime.get(Calendar.MINUTE));
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.ALARM_REPEAT_INTERVAL,repeatInterval);
        intent.putExtra(Constants.END_TIME_HOUR,endTime.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(Constants.END_TIME_MINUTE,endTime.get(Calendar.MINUTE));
        PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, pi);
    }

    public void cancelAlarm(Context context,int requestCode){
        Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
