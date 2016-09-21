package il.ac.huji.phonetime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import il.ac.huji.phonetime.blocking.BlockedAppsActivity;

public class MainActivity extends AppCompatActivity implements ValueEventListener {

    private static final long INTERVAL_TEN_SECONDS = 10 * 1000;
    private static final String TAG = MainActivity.class.getSimpleName();

    private enum TimeFrame {DAY, WEEK, MONTH}
    private TimeFrame selectedTimeFrame;
    private StatsFragment currentFragment;
    private StatsFragment[] allFragments = new StatsFragment[3];
    private HashMap<String, int[]>[] dataMaps = (HashMap<String, int[]>[]) new HashMap[TimeFrame.values().length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseManager.init(getBaseContext(), getContentResolver());
        selectedTimeFrame = TimeFrame.DAY;
        setFragment(savedInstanceState);
        setBlockButton();
        setToggles();
        scheduleAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i=0; i<TimeFrame.values().length; i++){
            dataMaps[i] = null;
        }
        updateFragment();
    }

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
                INTERVAL_TEN_SECONDS, pIntent);
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

            // Create new fragments to be placed in the activity layout
            allFragments[0] = PieChartFragment.newInstance();
            allFragments[1] = ListFragment.newInstance();
            allFragments[2] = TrendsFragment.newInstance();

            showFragment(0);
        }
    }

    private void showFragment(int frag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        currentFragment = allFragments[frag];
        if (currentFragment.isAdded()) ft.show(currentFragment);
        else ft.add(R.id.fragment_container, currentFragment);
        // Hide other fragments
        for (int i = 0; i<allFragments.length; i++){
            if(i != frag && allFragments[i].isAdded())
                ft.hide(allFragments[i]);
        }
        ft.commit();
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

    private void setToggles(){
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
        RadioGroup group = ((RadioGroup)view.getParent());
        int checkedId = view.getId();
        if(group.getCheckedRadioButtonId() == checkedId) { //already checked
            ((ToggleButton) view).setChecked(!((ToggleButton) view).isChecked());
            return;
        }
        ((RadioGroup)view.getParent()).check(checkedId);
        //replace fragment
        switch (checkedId){
            case R.id.btn_pie:
                showFragment(0);
                break;
            case R.id.btn_list:
                showFragment(1);
                break;
            case R.id.btn_trends:
                showFragment(2);
                break;
            default:
                return;
        }
        updateFragment();
    }

    public void onTimeFrameToggle(View view){
        RadioGroup group = ((RadioGroup)view.getParent());
        int checkedId = view.getId();
        if(group.getCheckedRadioButtonId() == checkedId) { //already checked
            ((ToggleButton) view).setChecked(!((ToggleButton) view).isChecked());
            return;
        }
        ((RadioGroup)view.getParent()).check(checkedId);
        switch (checkedId){
            case R.id.btn_day:
                selectedTimeFrame = TimeFrame.DAY;
                break;
            case R.id.btn_week:
                selectedTimeFrame = TimeFrame.WEEK;
                break;
            default:
                selectedTimeFrame = TimeFrame.MONTH;
                break;
        }
        updateFragment();
    }

    private void updateFragment(){
        currentFragment.showLoading();
        if(dataMaps[selectedTimeFrame.ordinal()] == null){
            dataMaps[selectedTimeFrame.ordinal()] = new HashMap<>();
            FirebaseManager.getUsesList(this);
        }else{
            currentFragment.update(dataMaps[selectedTimeFrame.ordinal()]);
            Log.d(TAG, "Updating fragment.");
        }
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        HashMap<String, int[]> dataMap = dataMaps[selectedTimeFrame.ordinal()];
        Calendar today = GregorianCalendar.getInstance();
        dataMap.clear();

        for (DataSnapshot useSnapshot: snapshot.getChildren()) {
            Use use = useSnapshot.getValue(Use.class);
            Calendar useTime = new GregorianCalendar();
            useTime.setTimeInMillis(use.timeStamp);
            switch (selectedTimeFrame){
                case DAY:
                    if(compareDates(useTime, today)){
                        if(dataMap.containsKey(use.packageName)){
                            dataMap.get(use.packageName)[useTime.get(Calendar.HOUR_OF_DAY)] += 10;
                        }else{
                            int[] times = new int[24];
                            times[useTime.get(Calendar.HOUR_OF_DAY)] = 10;
                            dataMap.put(use.packageName, times);
                        }
                    }
                    break;
                case WEEK:
                    if(useTime.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)
                            && useTime.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
                        if(dataMap.containsKey(use.packageName)){
                            dataMap.get(use.packageName)[useTime.get(Calendar.DAY_OF_WEEK)] += 10;
                        }else{
                            int[] times = new int[7];
                            times[useTime.get(Calendar.DAY_OF_WEEK)] = 10;
                            dataMap.put(use.packageName, times);
                        }
                    }
                    break;
                case MONTH:
                    if(useTime.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                            && useTime.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
                        if(dataMap.containsKey(use.packageName)){
                            dataMap.get(use.packageName)[useTime.get(Calendar.DAY_OF_MONTH)] += 10;
                        }else{
                            int[] times = new int[useTime.getActualMaximum(Calendar.DAY_OF_MONTH)];
                            times[useTime.get(Calendar.DAY_OF_MONTH)] = 10;
                            dataMap.put(use.packageName, times);
                        }
                    }
                    break;
            }
        }
        currentFragment.update(dataMap);
        Log.d(TAG, "Updating fragment.");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "onCancelled", databaseError.toException());
    }

    private Calendar zeroTime(Calendar time){
        Calendar zeroed = new GregorianCalendar();
        zeroed.setTimeInMillis(time.getTimeInMillis());
        zeroed.set(Calendar.HOUR_OF_DAY, 0);
        zeroed.set(Calendar.MINUTE, 0);
        zeroed.set(Calendar.SECOND, 0);
        zeroed.set(Calendar.MILLISECOND, 0);
        return zeroed;
    }

    private boolean compareDates(Calendar a, Calendar b){
        return a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH)
                && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
    }
}

