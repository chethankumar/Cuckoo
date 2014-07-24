package com.example.chethan.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.CharArrayReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class TimeSpeakerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "TimeSpeaker";
    private TextToSpeech textToSpeech;
    private PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("OnReceive of Time Speaker");
        Timber.d("End time is "+intent.getExtras().getInt(Constants.END_TIME_HOUR) +" : "+intent.getExtras().getInt(Constants.END_TIME_MINUTE));
        Calendar calendar = Calendar.getInstance();
        if (intent != null && intent.getExtras().getInt(Constants.END_TIME_HOUR) <= calendar.get(Calendar.HOUR_OF_DAY)
                && (intent.getExtras().getInt(Constants.END_TIME_MINUTE) < calendar.get(Calendar.MINUTE))) {
            CancelAlarm(context);
            Timber.d("Cancelling the time speaker alarm");
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        wl.acquire();
        //speak the current time
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.UK);
                    Format formatter = new SimpleDateFormat("h:mm a");
                    String date = formatter.format(new Date());
                    textToSpeech.speak("The time is " + date, TextToSpeech.QUEUE_FLUSH, null);

                }

            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                wl.release();
            }

            @Override
            public void onError(String s) {

            }
        });
        // wl.release();
    }

    @DebugLog
    public void SetAlarm(Context context, int interval, Calendar endTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeSpeakerBroadcastReceiver.class);
        intent.putExtra(Constants.END_TIME_HOUR,endTime.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(Constants.END_TIME_MINUTE,endTime.get(Calendar.MINUTE));
        PendingIntent pi = PendingIntent.getBroadcast(context, 21, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval * 1000, pi);
    }

    @DebugLog
    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, TimeSpeakerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 21, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
