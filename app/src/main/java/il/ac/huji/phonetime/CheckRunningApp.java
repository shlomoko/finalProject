package il.ac.huji.phonetime;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import il.ac.huji.phonetime.blocking.Rule;
import il.ac.huji.phonetime.blocking.RuleAfter;
import il.ac.huji.phonetime.blocking.RuleBetween;

public class CheckRunningApp extends IntentService implements ValueEventListener {

    public CheckRunningApp() {
        super("CheckRunningApp");
    }

    static int notificationDelay = 0;
    String currentApp;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        }

        Date now = new Date();
        writeNewUser(currentApp, now.getTime());
        checkIfBlocked(currentApp);
    }

    private void writeNewUser(String packageName, long timeStamp) {
        Use use = new Use(packageName, timeStamp);
        if (!(packageName.toLowerCase().contains("desktop") ||
                packageName.toLowerCase().contains("launcher"))) {
            FirebaseManager.addUse(use);
        }
    }

    private void checkIfBlocked(String pkgName) {
        Map<String, Rule> blocked = BaseActivity.blockedApps;
        if (notificationDelay == 0) {
            for(Map.Entry<String, Rule> entry : blocked.entrySet()) {
                if (entry.getKey().replace('-', '.').equals(pkgName)) {
                    Rule rule = entry.getValue();
                    if (rule instanceof RuleAfter){
                        RuleAfter ruleAfter = (RuleAfter) rule;
                        Map<String, int[]> uses = MainActivity.dataMaps[ruleAfter.getFrameVal().ordinal()];
                        // TODO - get real time data ^
                        int secsUsed = Utils.sumArray(uses.get(pkgName));
                        boolean isPassed = false;
                        if (ruleAfter.getUnitsVal() == RuleAfter.TimeUnits.MINS){
                            isPassed = secsUsed / 60 >= ruleAfter.getTime();
                        }else if (ruleAfter.getUnitsVal() == RuleAfter.TimeUnits.HOURS){
                            isPassed = secsUsed / 3600 >= ruleAfter.getTime();
                        }
                        if (isPassed){
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                            builder.setContentTitle("STOP USING THIS APP")
                                    .setContentText("you have surpassed the amount defined!")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(Notification.PRIORITY_HIGH);
                            notificationDelay = 6; // set delay to 6*10 seconds = 1 minute
                        }
                    } else if (rule instanceof RuleBetween){
                        Calendar now = GregorianCalendar.getInstance();
                        RuleBetween ruleBetween = (RuleBetween) rule;
                        Calendar from = new GregorianCalendar();
                        from.set(Calendar.HOUR_OF_DAY, ruleBetween.getFromHours());
                        from.set(Calendar.MINUTE, ruleBetween.getFromMinutes());
                        Calendar to = new GregorianCalendar();
                        to.set(Calendar.HOUR_OF_DAY, ruleBetween.getToHours());
                        to.set(Calendar.MINUTE, ruleBetween.getToMinutes());
                        if(now.after(from) && now.before(to)){
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                            builder.setContentTitle("STOP USING THIS APP")
                                    .setContentText("you do not want to be using this the app now!!")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(Notification.PRIORITY_HIGH);
                            notificationDelay = 6; // set delay to 6*10 seconds = 1 minute
                        }
                    }
                }
            }
        } else {
            notificationDelay--;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "onCancelled", databaseError.toException());
    }
}
