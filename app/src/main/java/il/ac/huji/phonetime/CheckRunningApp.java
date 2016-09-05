package il.ac.huji.phonetime;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Shlomo on 28/07/2016.
 */
public class CheckRunningApp extends IntentService {
    public CheckRunningApp() {
        super("CheckRunningApp");
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    String currentApp;
    long currentTime;
    @Override
    protected void onHandleIntent(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);//"usagestats"
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(
                            mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity .getPackageName();

        }
//        Context mContext = this; //##################### TODO NOT SURE ###########################
//        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
        //Log.i("current_app", currentApp);//tasks.get(0).processName);
//        Log.i("CheckRunningApp", "Service running");
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( currentApp, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        Date date = new Date();
        writeNewUser(currentApp, date.getTime());
        //Log.i("current_app_name", applicationName);//tasks.get(0).processName);
    }
    String s;
    private void writeNewUser(String packageName, long timeStamp) {
        use user = new use(packageName, timeStamp);
        s = String.valueOf(timeStamp);
        if (!(packageName.contains("desktop") || packageName.contains("Desktop"))) {
            mRootRef.child("uses").child(s).setValue(user);
        }
    }
}
