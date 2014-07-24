package com.cuckoo;

import com.example.chethan.test.TimeSpeakerBroadcastReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by chethan on 17/07/14.
 */
public class UserAnnouncer extends BaseAnnouncer {
    ArrayList<String> days;
    boolean active = false;

    public UserAnnouncer(Calendar startTime, Calendar endTime, int announceInterval, ArrayList<String> days) {
        super(startTime, endTime, announceInterval);
        this.days = days;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public void print() {
        Timber.d("======== User Announcer=========");
        Timber.d("start time :: Hour - " + this.getStartTime().get(Calendar.HOUR_OF_DAY) + " Min - " + this.getStartTime().get(Calendar.MINUTE));
        Timber.d("end time :: Hour - " + this.getEndTime().get(Calendar.HOUR_OF_DAY) + " Min - " + this.getEndTime().get(Calendar.MINUTE));
        Timber.d("Announce interval :: " + this.getAnnounceInterval());
        Timber.d("Days" + this.getDays().toString());
        Timber.d("==========================");
    }
}
