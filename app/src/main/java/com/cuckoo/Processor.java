package com.cuckoo;

import android.content.Context;
import android.provider.SyncStateContract;

import com.example.chethan.test.AlarmBroadcastReceiver;
import com.example.chethan.test.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.kots.mob.complex.preferences.ComplexPreferences;
import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Created by chethan on 17/07/14.
 */
public class Processor {

    public static void createAlarms(Context context) {
        if (retrieveAlarms(context) != null) {
            for (UserAnnouncer userAnnouncer : retrieveAlarms(context)) {
                for (String day : userAnnouncer.getDays()) {
                    Announcer announcer = new Announcer(userAnnouncer.getStartTime(), userAnnouncer.getEndTime(),
                            userAnnouncer.getAnnounceInterval(), day);
                    Timber.d("announcer object : ");
                    announcer.print();
                    Calendar now = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    if (now.get(Calendar.DAY_OF_WEEK) > Announcer.getDayId(day)) {
                        now.add(Calendar.DATE, (7 - (now.get(Calendar.DAY_OF_WEEK) - Announcer.getDayId(day))));
                    } else if (now.get(Calendar.DAY_OF_WEEK) < Announcer.getDayId(day)) {
                        now.add(Calendar.DATE, (Announcer.getDayId(day)) - now.get(Calendar.DAY_OF_WEEK));
                    } else {//means its today
                        if (announcer.getStartTime().get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY)) {
                            now.add(Calendar.DATE, 7);
                        } else if (announcer.getStartTime().get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY)) {
                            if (announcer.getStartTime().get(Calendar.MINUTE) <= now.get(Calendar.MINUTE)) {
                                now.add(Calendar.DATE, 7);
                            }
                        }
                    }
                    now.set(Calendar.HOUR_OF_DAY, announcer.getStartTime().get(Calendar.HOUR_OF_DAY));
                    now.set(Calendar.MINUTE, announcer.getStartTime().get(Calendar.MINUTE));
                    announcer.setStartTime(now);
                    end.set(Calendar.DATE, now.get(Calendar.DATE));
                    end.set(Calendar.HOUR_OF_DAY, announcer.getEndTime().get(Calendar.HOUR_OF_DAY));
                    end.set(Calendar.MINUTE, announcer.getEndTime().get(Calendar.MINUTE));
                    announcer.setEndTime(end);
                    Timber.d("Alarm Start time " + announcer.getStartTime().getTime().toString());
                    Timber.d("Alarm End time " + announcer.getEndTime().getTime().toString());
                    AlarmBroadcastReceiver alarmBroadcastReceiver = new AlarmBroadcastReceiver();
                    if (userAnnouncer.isActive()) {
                        alarmBroadcastReceiver.setAlarm(context.getApplicationContext(), 1000 * 60 * 60 * 24, announcer.getAnnounceInterval(),
                                announcer.getIdentifier(), announcer.getStartTime(), announcer.getEndTime());
                    }
                }
            }
        }
    }

    public static void removeAllAlarms(Context context) {
        Timber.d("Removing all alarms");
        if (retrieveAlarms(context) != null) {
            for (UserAnnouncer userAnnouncer : retrieveAlarms(context)) {
                for (String day : userAnnouncer.getDays()) {
                    Announcer announcer = new Announcer(userAnnouncer.getStartTime(), userAnnouncer.getEndTime(),
                            userAnnouncer.getAnnounceInterval(), day);
                    Timber.d("announcer object : ");
                    announcer.print();
                    AlarmBroadcastReceiver alarmBroadcastReceiver = new AlarmBroadcastReceiver();
                    alarmBroadcastReceiver.cancelAlarm(context, announcer.getIdentifier());
                }
            }
        }
    }

    @DebugLog
    public static void persistAlarms(Context context) {
        UserAnnouncerList announcerList = new UserAnnouncerList();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 23);
        startTime.set(Calendar.MINUTE, 50);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 51);

        ArrayList<String> dayList = new ArrayList<String>();
        dayList.add(Constants.MONDAY);
        dayList.add(Constants.WEDNESDAY);
        dayList.add(Constants.THURSDAY);
        dayList.add(Constants.SATURDAY);
        UserAnnouncer userAnnouncer = new UserAnnouncer(startTime, endTime, 4, dayList);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.set(Calendar.HOUR_OF_DAY, 23);
        startTime2.set(Calendar.MINUTE, 50);

        Calendar endTime2 = Calendar.getInstance();
        endTime2.set(Calendar.HOUR_OF_DAY, 23);
        endTime2.set(Calendar.MINUTE, 51);

        ArrayList<String> dayList2 = new ArrayList<String>();
        dayList2.add(Constants.SUNDAY);
        dayList2.add(Constants.FRIDAY);
        dayList2.add(Constants.TUESDAY);
        UserAnnouncer userAnnouncer2 = new UserAnnouncer(startTime2, endTime2, 10, dayList2);

        ArrayList<UserAnnouncer> myList = new ArrayList<UserAnnouncer>();
        myList.add(userAnnouncer);
        myList.add(userAnnouncer2);
        announcerList.setUserAnnouncerList(myList);
        ComplexPreferences preferences = ComplexPreferences.getComplexPreferences(context, Constants.PREFERENCES, Context.MODE_PRIVATE);
        preferences.putObject(Constants.ALARM_LIST, announcerList);
        preferences.commit();
    }

    public static void createNewAlarm(Context context, UserAnnouncer userAnnouncer) {
        UserAnnouncerList userAnnouncerList;
        ComplexPreferences preferences = ComplexPreferences.getComplexPreferences(context, Constants.PREFERENCES, Context.MODE_PRIVATE);
        userAnnouncerList = preferences.getObject(Constants.ALARM_LIST, UserAnnouncerList.class);
        ArrayList<UserAnnouncer> list;
        if (userAnnouncerList==null){
            list=new ArrayList<UserAnnouncer>();
            userAnnouncerList = new UserAnnouncerList();
        }else{
            list = userAnnouncerList.getUserAnnouncerList();
        }
        list.add(userAnnouncer);
        userAnnouncerList.setUserAnnouncerList(list);
        preferences.putObject(Constants.ALARM_LIST, userAnnouncerList);
        preferences.commit();
        //set relevant alarms
        createAlarms(context);
    }

    @DebugLog
    public static ArrayList<UserAnnouncer> retrieveAlarms(Context context) {
        UserAnnouncerList userAnnouncerList;
        ComplexPreferences preferences = ComplexPreferences.getComplexPreferences(context, Constants.PREFERENCES, Context.MODE_PRIVATE);
        userAnnouncerList = preferences.getObject(Constants.ALARM_LIST, UserAnnouncerList.class);

        if (userAnnouncerList != null) {
            for (UserAnnouncer announcer : userAnnouncerList.getUserAnnouncerList()) {
                announcer.print();
            }
            return userAnnouncerList.getUserAnnouncerList();
        }
        return null;

    }

    public static UserAnnouncer getAlarm(Context context, int position) {
        UserAnnouncerList userAnnouncerList;
        ComplexPreferences preferences = ComplexPreferences.getComplexPreferences(context, Constants.PREFERENCES, Context.MODE_PRIVATE);
        userAnnouncerList = preferences.getObject(Constants.ALARM_LIST, UserAnnouncerList.class);
        return userAnnouncerList.getUserAnnouncerList().get(position);
    }

    public static void updateAlarms(Context context, int position, UserAnnouncer userAnnouncer) {
        //update data in store
        UserAnnouncerList userAnnouncerList;
        ComplexPreferences preferences = ComplexPreferences.getComplexPreferences(context, Constants.PREFERENCES, Context.MODE_PRIVATE);
        userAnnouncerList = preferences.getObject(Constants.ALARM_LIST, UserAnnouncerList.class);
        ArrayList<UserAnnouncer> list = userAnnouncerList.getUserAnnouncerList();
        list.remove(position);
        list.add(position, userAnnouncer);
        userAnnouncerList.setUserAnnouncerList(list);
        preferences.putObject(Constants.ALARM_LIST, userAnnouncerList);
        preferences.commit();
        // remove all alarms
        removeAllAlarms(context);
        //set relevant alarms
        createAlarms(context);
    }
}
