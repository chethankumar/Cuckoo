package com.cuckoo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Created by chethan on 17/07/14.
 */
abstract class BaseAnnouncer {

    private Calendar startTime;
    private Calendar endTime;
    private long duration;
    private int announceInterval;

    @DebugLog
    public BaseAnnouncer(Calendar startTime, Calendar endTime, int announceInterval) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.announceInterval = announceInterval;
        this.duration = (endTime.getTimeInMillis() - startTime.getTimeInMillis());
        Timber.d("duration :: "+this.duration +" millisecs");
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public int getAnnounceInterval() {
        return announceInterval;
    }

    public void setAnnounceInterval(int announceInterval) {
        this.announceInterval = announceInterval;
    }
}
