package il.ac.huji.phonetime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements PieChartFragment.OnFragmentInteractionListener, TrendsFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        setFragment(savedInstanceState);
        setBlockButton();
        setToggels();
        scheduleAlarm();
    }
    private static final long INTERVAL_ONE_MINUTE = 60 * 1000;
    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmRec.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmRec.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                2000, pIntent);
    }

    // https://developer.android.com/training/basics/fragments/fragment-ui.html
    private void setFragment(Bundle savedInstanceState){
        // Check that the activity is using the layout version with the fragment_container
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state, then we don't need to do
            // anything and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            PieChartFragment firstFragment = new PieChartFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            HashMap<String, int[]> map = new HashMap<>();
            map.put("Facebbok", new int[]{2,0,5,3});
            map.put("Whatsapp", new int[]{4,1,0,2});
            map.put("Calendar", new int[]{8,2,0,1});
            map.put("Chrome", new int[]{2,5,0,1});

            Bundle args = new Bundle();
            args.putSerializable(PieChartFragment.APP_TIMES, map);
            args.putInt(PieChartFragment.TOTAL_TIME, 36);

            firstFragment.setArguments(args);


            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    private void setBlockButton(){
        Button but = (Button) findViewById(R.id.blockButton);
        if (but != null) {
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BlockedAppsActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    private void setToggels(){
        RadioGroup group = (RadioGroup)findViewById(R.id.typesGroup);
        if (group != null) {
            group.setOnCheckedChangeListener(ToggleListener);
        }
        group = (RadioGroup)findViewById(R.id.timeFrameGroup);
        if (group != null) {
            group.setOnCheckedChangeListener(ToggleListener);
        }
    }

    public void onTypeToggle(View view){
        int checkedId = view.getId();
        ((RadioGroup)view.getParent()).check(checkedId);
        //replace fragment
        Fragment newFrag;// = null;
        switch (checkedId){
            case R.id.btn_pie:
                newFrag = new PieChartFragment();
                break;
            case R.id.btn_list:
                newFrag = null;//TODO
                break;
            case R.id.btn_trends:
                newFrag = new TrendsFragment();
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newFrag).commit();
    }

    public void onTimeFrameToggle(View view){
        int checkedId = view.getId();
        ((RadioGroup)view.getParent()).check(checkedId);
        //TODO send this value to current fragment
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

