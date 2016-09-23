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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import il.ac.huji.phonetime.blocking.Rule;
import il.ac.huji.phonetime.blocking.RuleAfter;
import il.ac.huji.phonetime.blocking.RuleBetween;

public class CheckRunningApp extends IntentService implements ValueEventListener {

    public CheckRunningApp() {
        super("CheckRunningApp");
    }

    static int giveMeTen = 0;
    String currentApp;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);

            /*Collections.sort(appList, new Comparator<UsageStats>() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public int compare(UsageStats lhs, UsageStats rhs) {
                    return lhs.getLastTimeUsed() - rhs.getLastTimeUsed() > 0? 1:-1;
                }
            });*/

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey())
                                            .getPackageName();
                }


            }
        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();

        }

        //final String applicationName = Utils.getAppName(getApplicationContext(), currentApp);
        Date date = new Date();
        writeNewUser(currentApp, date.getTime());
        checkIfBlocked(currentApp);
        //Log.i("current_app_name", applicationName);//tasks.get(0).processName);
    }

    private void writeNewUser(String packageName, long timeStamp) {
        Use use = new Use(packageName, timeStamp);
        if (!(packageName.toLowerCase().contains("desktop") ||
                packageName.toLowerCase().contains("launcher"))) {
            FirebaseManager.addUse(use);
        }
    }

    private void checkIfBlocked(String packageName) {
        List<Rule> blocked = BaseActivity.blockedApps;
        if (blocked != null && giveMeTen == 0) {
            for (Rule rule : blocked) {
                if (rule.toString().replace('-', '.') == packageName) {
                    if (rule instanceof RuleAfter && ((RuleAfter) rule).getTime() < TimeUsed) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentTitle("STOP USING THIS APP")
                                .setContentText("you have surpassed the amount defined!")
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setPriority(Notification.PRIORITY_HIGH);
                    } else if (rule instanceof RuleBetween) {
                        int from = ((RuleBetween) rule).getFromHours() * 60 + ((RuleBetween) rule).getFromSeconds();
                        int to = ((RuleBetween) rule).getToHours() * 60 + ((RuleBetween) rule).getToSeconds();
                        if (currentTime > from && currentTime < to) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                            builder.setContentTitle("STOP USING THIS APP")
                                    .setContentText("you do not want to be using this the app now!!")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(Notification.PRIORITY_HIGH);
                        }
                    }
                }
            }
        } else {
            giveMeTen--;
        }
    }

    @Override
    public void

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "onCancelled", databaseError.toException());
    }
}
