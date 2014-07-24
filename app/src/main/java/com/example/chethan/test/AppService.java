package com.example.chethan.test;

import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cuckoo.Processor;

import hugo.weaving.DebugLog;

/**
 * Created by chethan on 15/07/14.
 */
public class AppService extends Service {

    AlarmManager alarmManager;
    AlarmBroadcastReceiver alarmBroadcastReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    @DebugLog
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("service","onstart command");
        Toast.makeText(getApplicationContext(),"service",Toast.LENGTH_SHORT).show();
        Processor.removeAllAlarms(getApplicationContext());
        Processor.createAlarms(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
