package il.ac.huji.phonetime;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckRunningApp extends IntentService {

    public CheckRunningApp() {
        super("CheckRunningApp");
    }

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
        //Log.i("current_app_name", applicationName);//tasks.get(0).processName);
    }

    private void writeNewUser(String packageName, long timeStamp) {
        Use use = new Use(packageName, timeStamp);
        if (!(packageName.toLowerCase().contains("desktop") ||
                packageName.toLowerCase().contains("launcher"))) {
            FirebaseManager.addUse(use);
        }
    }
}
