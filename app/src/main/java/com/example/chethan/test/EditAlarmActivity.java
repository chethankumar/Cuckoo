package com.example.chethan.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.cuckoo.Processor;
import com.cuckoo.UserAnnouncer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class EditAlarmActivity extends Activity {

    @InjectView(R.id.startTimeEditTextView)
    TextView mStartTimeEditTextView;
    @InjectView(R.id.startAmPmEditTV)
    TextView mStartAmPmEditTV;
    @InjectView(R.id.startText)
    TextView mStartText;
    @InjectView(R.id.endTimeEditTextView)
    TextView mEndTimeEditTextView;
    @InjectView(R.id.endAmPmEditTV)
    TextView mEndAmPmEditTV;
    @InjectView(R.id.endText)
    TextView mEndText;
    @InjectView(R.id.repeatIntervalText)
    TextView mRepeatIntervalText;
    @InjectView(R.id.intervalSeekBar)
    SeekBar mIntervalSeekBar;
    @InjectView(R.id.repeatIntervalTextView)
    TextView repeatIntervalTextView;
    @InjectView(R.id.mondayCheckBox)
    CheckBox mMondayCheckBox;
    @InjectView(R.id.tuesdayCheckBox)
    CheckBox mTuesdayCheckBox;
    @InjectView(R.id.wednesdayCheckBox)
    CheckBox mWednesdayCheckBox;
    @InjectView(R.id.thursdayCheckBox)
    CheckBox mThursdayCheckBox;
    @InjectView(R.id.fridayCheckBox)
    CheckBox mFridayCheckBox;
    @InjectView(R.id.saturdayCheckBox)
    CheckBox mSaturdayCheckBox;
    @InjectView(R.id.sundayCheckBox)
    CheckBox mSundayCheckBox;
    @InjectView(R.id.cancelBtn)
    Button cancelButton;
    @InjectView(R.id.saveButton)
    Button saveButton;

    private boolean editingMode=false;
    int position=-1;
    UserAnnouncer announcer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        ButterKnife.inject(this);

        if (getIntent()!=null) {
            if (getIntent().getExtras().get(Constants.EDIT_ALARM) != null &&
                    getIntent().getExtras().getBoolean(Constants.EDIT_ALARM)) {
                //edit
                editingMode=true;
                position = getIntent().getExtras().getInt(Constants.ALARM_POSITION);
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                startTime.setTimeInMillis(getIntent().getExtras().getLong(Constants.START_TIME));

                endTime.setTimeInMillis(getIntent().getExtras().getLong(Constants.END_TIME));

                int interval= getIntent().getExtras().getInt(Constants.ALARM_REPEAT_INTERVAL);
                ArrayList<String> dayList = getIntent().getExtras().getStringArrayList(Constants.ALARM_DAYS);
                announcer = new UserAnnouncer(startTime,endTime,interval,dayList);
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
                SimpleDateFormat ampmFormat = new SimpleDateFormat("a");

                mStartTimeEditTextView.setText(dateFormat.format(announcer.getStartTime().getTime()));
                mStartAmPmEditTV.setText(ampmFormat.format(announcer.getStartTime().getTime()));
                mEndTimeEditTextView.setText(dateFormat.format(announcer.getEndTime().getTime()));
                mEndAmPmEditTV.setText(ampmFormat.format(announcer.getEndTime().getTime()));
                repeatIntervalTextView.setText(""+announcer.getAnnounceInterval());
                setAlarmDays(dayList);
                position=getAlarmPosition(announcer);
            } else {
                //new
                editingMode=false;
            }
        }


        mIntervalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    repeatIntervalTextView.setText("1");
                } else {
                    repeatIntervalTextView.setText("" + i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setAlarmDays(ArrayList<String> dayList) {
        for (String day:dayList){
            if (day.equalsIgnoreCase(Constants.MONDAY)){
                mMondayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.TUESDAY)){
                mTuesdayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.WEDNESDAY)){
                mWednesdayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.THURSDAY)){
                mThursdayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.FRIDAY)){
                mFridayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.SATURDAY)){
                mSaturdayCheckBox.setChecked(true);
            }else if(day.equalsIgnoreCase(Constants.SUNDAY)){
                mSundayCheckBox.setChecked(true);
            }
        }
    }

    private ArrayList<String> getSelectedDaysList(){
        ArrayList<String> daysList = new ArrayList<String>();
        if (mMondayCheckBox.isChecked()){
            daysList.add(Constants.MONDAY);
        }
        if (mTuesdayCheckBox.isChecked()){
            daysList.add(Constants.TUESDAY);
        }
        if (mWednesdayCheckBox.isChecked()){
            daysList.add(Constants.WEDNESDAY);
        }
        if (mThursdayCheckBox.isChecked()){
            daysList.add(Constants.THURSDAY);
        }
        if (mFridayCheckBox.isChecked()){
            daysList.add(Constants.FRIDAY);
        }
        if (mSaturdayCheckBox.isChecked()){
            daysList.add(Constants.SATURDAY);
        }
        if (mSundayCheckBox.isChecked()){
            daysList.add(Constants.SUNDAY);
        }
        Timber.d("days list "+daysList.toString());
        return daysList;
    }

    @DebugLog
    private int getAlarmPosition(UserAnnouncer userAnnouncer){
        ArrayList<UserAnnouncer> userAnnouncerArrayList = Processor.retrieveAlarms(getApplicationContext());
        for (int i=0;i<userAnnouncerArrayList.size();i++){
            UserAnnouncer obj = userAnnouncerArrayList.get(i);
            if(userAnnouncer.getStartTime().get(Calendar.HOUR_OF_DAY)==obj.getStartTime().get(Calendar.HOUR_OF_DAY)
                    && userAnnouncer.getStartTime().get(Calendar.MINUTE)==obj.getStartTime().get(Calendar.MINUTE)
                    && userAnnouncer.getEndTime().get(Calendar.HOUR_OF_DAY)==obj.getEndTime().get(Calendar.HOUR_OF_DAY)
                    &&  userAnnouncer.getEndTime().get(Calendar.MINUTE)==obj.getEndTime().get(Calendar.MINUTE) ){
                boolean flag=false;
                if (obj.getDays().size()==userAnnouncer.getDays().size()){
                    for (String day:obj.getDays()){
                        if(!userAnnouncer.getDays().contains(day)){
                            flag=true;
                        }

                    }
                    if (!flag) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @OnClick(R.id.cancelBtn)
    void cancelClick(){
        EditAlarmActivity.this.finish();
    }

    @OnClick(R.id.saveButton)
    void saveClick(){

            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            try {
                startCal.setTimeInMillis(dateFormat.parse(mStartTimeEditTextView.getText().toString()).getTime());
                if (mStartAmPmEditTV.getText().toString().equalsIgnoreCase("am")){
                    startCal.set(Calendar.AM_PM,Calendar.AM);
                }else{
                    startCal.set(Calendar.AM_PM,Calendar.PM);
                }
                endCal.setTimeInMillis(dateFormat.parse(mEndTimeEditTextView.getText().toString()).getTime());
                if (mEndAmPmEditTV.getText().toString().equalsIgnoreCase("am")){
                    endCal.set(Calendar.AM_PM,Calendar.AM);
                }else{
                    endCal.set(Calendar.AM_PM,Calendar.PM);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            UserAnnouncer newUserAnnouncer = new UserAnnouncer(startCal, endCal,
                    Integer.parseInt(repeatIntervalTextView.getText().toString()), getSelectedDaysList());
            newUserAnnouncer.print();
        if (!editingMode){
            Processor.createNewAlarm(getApplicationContext(), newUserAnnouncer);
        }else if(position!=-1) {
            Processor.updateAlarms(getApplicationContext(),position,newUserAnnouncer);
        }else {
            Timber.d("its in editing mode and position is not updated properly");
        }

        EditAlarmActivity.this.finish();
    }

    @OnClick(R.id.startTimeEditTextView)
    void startTimeClick(){
        TimePickerDialog pickerDialog = new TimePickerDialog();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTimeInMillis(dateFormat.parse(mStartTimeEditTextView.getText().toString()).getTime());
            if (mStartAmPmEditTV.getText().toString().equalsIgnoreCase("am")){
                cal.set(Calendar.AM_PM,Calendar.AM);
            }else{
                cal.set(Calendar.AM_PM,Calendar.PM);
            }
        }catch (ParseException pe){
            Timber.d(pe.getMessage());
        }
        pickerDialog.setStartTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        pickerDialog.show(getFragmentManager(),"startTime");
        pickerDialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
                SimpleDateFormat ampmFormat = new SimpleDateFormat("a");
                mStartTimeEditTextView.setText(dateFormat.format(calendar.getTime()));
                mStartAmPmEditTV.setText(ampmFormat.format(calendar.getTime()));
                Timber.d("hour "+i +"min "+i2);
            }
        });
    }

    @OnClick(R.id.endTimeEditTextView)
    void endTimeClick(){
        TimePickerDialog pickerDialog = new TimePickerDialog();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTimeInMillis(dateFormat.parse(mEndTimeEditTextView.getText().toString()).getTime());
            if (mEndAmPmEditTV.getText().toString().equalsIgnoreCase("am")){
                cal.set(Calendar.AM_PM,Calendar.AM);
            }else{
                cal.set(Calendar.AM_PM,Calendar.PM);
            }
        }catch (ParseException pe){
            Timber.d(pe.getMessage());
        }
        pickerDialog.setStartTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        pickerDialog.show(getFragmentManager(),"endTime");
        pickerDialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
                SimpleDateFormat ampmFormat = new SimpleDateFormat("a");
                mEndTimeEditTextView.setText(dateFormat.format(calendar.getTime()));
                mEndAmPmEditTV.setText(ampmFormat.format(calendar.getTime()));
                Timber.d("hour "+i +"min "+i2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
