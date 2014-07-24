package com.example.chethan.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.cuckoo.CircleButton;
import com.cuckoo.Processor;
import com.cuckoo.UserAnnouncer;
import com.cuckoo.Utils;
import com.quentindommerc.superlistview.SuperListview;
import com.quentindommerc.superlistview.SwipeDismissListViewTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.ribot.easyadapter.EasyAdapter;
import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;


public class MyActivity extends Activity {

    TextToSpeech textToSpeech;

    @InjectView(R.id.alarmListView)
    SuperListview alarmListView;
    @InjectView(R.id.addAlarm)
    CircleButton addAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

        Intent intent = new Intent(getApplicationContext(), AppService.class);
        getApplicationContext().startService(intent);

        addAlarm.setColor(Color.RED);
        if (Build.VERSION.SDK_INT>19){
            Timber.d("setting elevation");
            addAlarm.setElevation(1);
        }
        //dummy data
       // Processor.persistAlarms(getApplicationContext());

        if (Processor.retrieveAlarms(getApplicationContext())!=null){
            initialiseAlarmListView();
        }

    }

    @OnClick(R.id.addAlarm)
    void addAlarm(){
        Timber.d("add alarm");
        Intent newAlarm = new Intent(getApplicationContext(),EditAlarmActivity.class);
        newAlarm.putExtra(Constants.EDIT_ALARM, false);
        startActivity(newAlarm);
    }

    private void initialiseAlarmListView() {
        if (Processor.retrieveAlarms(getApplicationContext())!=null){
            alarmListView.setAdapter(new EasyAdapter<UserAnnouncer>(getApplicationContext(),AlarmCardViewHolder.class,Processor.retrieveAlarms(getApplicationContext())));
        }
        alarmListView.setupSwipeToDismiss(new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int i) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] ints) {

            }
        },true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseAlarmListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @LayoutId(R.layout.alarm_card)
    public static class AlarmCardViewHolder extends ItemViewHolder<UserAnnouncer>{

        @ViewId(R.id.mondayTextView)
        TextView mondayTextView;

        @ViewId(R.id.tuesdayTextView)
        TextView tuesdayTextView;

        @ViewId(R.id.wednesdayTextView)
        TextView wednesdayTextView;

        @ViewId(R.id.thursdayTextView)
        TextView thursdayTextView;

        @ViewId(R.id.fridayTextView)
        TextView fridayTextView;

        @ViewId(R.id.saturdayTextView)
        TextView saturdayTextView;

        @ViewId(R.id.sundayTextView)
        TextView sundayTextView;

        @ViewId(R.id.startTimeTextView)
        TextView startTimeTextView;

        @ViewId(R.id.endTimeTextView)
        TextView endTimeTextView;

        @ViewId(R.id.startAmPmTV)
        TextView startAmPmTV;

        @ViewId(R.id.endAmPmTV)
        TextView endAmPmTV;

        @ViewId(R.id.timeIntervalTV)
        TextView intervalTextView;

        @ViewId(R.id.alarmSwitch)
        Switch alarmSwitch;

        public AlarmCardViewHolder(View view){
            super(view);
            startTimeTextView.setTypeface(Utils.getLightTypeface(getContext()));
            endAmPmTV.setTypeface(Utils.getLightTypeface(getContext()));
            endTimeTextView.setTypeface(Utils.getLightTypeface(getContext()));
            startAmPmTV.setTypeface(Utils.getLightTypeface(getContext()));
            mondayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            tuesdayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            wednesdayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            thursdayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            fridayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            saturdayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            sundayTextView.setTypeface(Utils.getNormalTypeface(getContext()));
            intervalTextView.setTypeface(Utils.getLightTypeface(getContext()));

        }

        @Override
        public void onSetValues(final UserAnnouncer item, final PositionInfo positionInfo) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
            SimpleDateFormat ampmFormat = new SimpleDateFormat("a");

            startTimeTextView.setText(dateFormat.format(item.getStartTime().getTime()));
            startAmPmTV.setText(ampmFormat.format(item.getStartTime().getTime()));
            endTimeTextView.setText(dateFormat.format(item.getEndTime().getTime()));
            endAmPmTV.setText(ampmFormat.format(item.getEndTime().getTime()));
            intervalTextView.setText(""+item.getAnnounceInterval());
            alarmSwitch.setChecked(item.isActive());

            mondayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            tuesdayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            wednesdayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            thursdayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            fridayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            saturdayTextView.setTextColor(Color.parseColor("#ffbebebe"));
            sundayTextView.setTextColor(Color.parseColor("#ffbebebe"));

            for (String day:item.getDays()){
                if (day.equalsIgnoreCase(Constants.MONDAY)){
                    mondayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.TUESDAY)){
                    tuesdayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.WEDNESDAY)){
                    wednesdayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.THURSDAY)){
                    thursdayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.FRIDAY)){
                    fridayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.SATURDAY)){
                    saturdayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }else if(day.equalsIgnoreCase(Constants.SUNDAY)){
                    sundayTextView.setTextColor(Color.parseColor("#ff4d4d4d"));
                }
            }

            alarmSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setActive(alarmSwitch.isChecked());
                    Processor.updateAlarms(getContext(),positionInfo.getPosition(),item);
                }
            });
        }

        @Override
        public void onSetListeners() {
           getView().setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Timber.d("alarm click");
                   Processor.removeAllAlarms(getContext().getApplicationContext());
                   Intent editAlarm = new Intent(getContext(),EditAlarmActivity.class);
                   editAlarm.putExtra(Constants.EDIT_ALARM,true);
                   editAlarm.putExtra(Constants.START_TIME,getItem().getStartTime().getTimeInMillis());
                   editAlarm.putExtra(Constants.END_TIME,getItem().getEndTime().getTimeInMillis());
                   editAlarm.putExtra(Constants.ALARM_REPEAT_INTERVAL,getItem().getAnnounceInterval());
                   editAlarm.putStringArrayListExtra(Constants.ALARM_DAYS,getItem().getDays() );
                   editAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   getContext().startActivity(editAlarm);
               }
           });

        }
    }
}
