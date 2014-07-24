package com.cuckoo;

import com.example.chethan.test.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by chethan on 17/07/14.
 */
public class Announcer extends BaseAnnouncer {
    private int identifier;
    private String day;

    public Announcer(Calendar startTime, Calendar endTime, int announceInterval, String day) {
        super(startTime, endTime, announceInterval);

        String identifierString = String.valueOf(startTime.get(Calendar.HOUR_OF_DAY)) +
                String.valueOf(startTime.get(Calendar.MINUTE))+
                String.valueOf(endTime.get(Calendar.HOUR_OF_DAY))+
                String.valueOf(endTime.get(Calendar.MINUTE))+getDayId(day);
        this.identifier = Integer.parseInt(identifierString);
        this.day = day;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDay() {
        return day;
    }

    public void print(){
        Timber.d("========Announcer=========");
        Timber.d("identifier :: "+this.identifier);
        Timber.d("start time :: Hour - "+this.getStartTime().get(Calendar.HOUR_OF_DAY)+" Min - "+this.getStartTime().get(Calendar.MINUTE));
        Timber.d("end time :: Hour - "+this.getEndTime().get(Calendar.HOUR_OF_DAY)+" Min - "+this.getEndTime().get(Calendar.MINUTE));
        Timber.d("Announce interval :: "+this.getAnnounceInterval());
        Timber.d("day :: "+this.day);
        Timber.d("==========================");
    }

    public static int getDayId(String day){
        if (day.equalsIgnoreCase(Constants.MONDAY))
            return 2;
        else if(day.equalsIgnoreCase(Constants.TUESDAY))
            return 3;
        else if(day.equalsIgnoreCase(Constants.WEDNESDAY))
            return 4;
        else if(day.equalsIgnoreCase(Constants.THURSDAY))
            return 5;
        else if(day.equalsIgnoreCase(Constants.FRIDAY))
            return 6;
        else if(day.equalsIgnoreCase(Constants.SATURDAY))
            return 7;
        else if(day.equalsIgnoreCase(Constants.SUNDAY))
            return 1;
        return 9;
    }
}
