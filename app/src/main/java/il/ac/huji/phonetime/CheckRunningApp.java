package il.ac.huji.phonetime;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import il.ac.huji.phonetime.blocking.Rule;
import il.ac.huji.phonetime.blocking.RuleAfter;
import il.ac.huji.phonetime.blocking.RuleBetween;

public class CheckRunningApp extends IntentService implements ValueEventListener {

    public static Map<String, Rule> blockedApps = new HashMap<>();
    private static int notificationDelay = 0;
    private static String checkedPkg;
    private static RuleAfter checkedRule;

    public CheckRunningApp() {
        super(CheckRunningApp.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String currentApp = null;
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

        if (currentApp != null){
            Date now = new Date();
            writeNewUser(currentApp, now.getTime());
            checkIfBlocked(currentApp);
        }
    }

    private void writeNewUser(String packageName, long timeStamp) {
        Use use = new Use(packageName, timeStamp);
        if (!(packageName.toLowerCase().contains("desktop") ||
                packageName.toLowerCase().contains("launcher"))) {
            FirebaseManager.addUse(use);
        }
    }

    private void checkIfBlocked(String pkgName) {
        pkgName = pkgName.replace('.', '-');
        if (!blockedApps.containsKey(pkgName)) return;
        if (notificationDelay == 0) {
            Rule rule = blockedApps.get(pkgName);
            if (rule instanceof RuleAfter){
                checkedPkg = pkgName;
                checkedRule = (RuleAfter) rule;
                FirebaseManager.getUsesList(this, pkgName);
            } else if (rule instanceof RuleBetween){
                if(rule.isViolated()){
                    notify("you do not want to be using this the app now!!");
                }
            }
        } else {
            notificationDelay--;
        }
    }

    @Override
    public void onDataChange(DataSnapshot pkgSnapshot) {
        if (!pkgSnapshot.getKey().equals(checkedPkg)) return;
        int secsUsed = 0;
        Calendar today = GregorianCalendar.getInstance();
        for (DataSnapshot timeSnapshot: pkgSnapshot.getChildren()){
            Calendar useTime = new GregorianCalendar();
            useTime.setTimeInMillis(timeSnapshot.getValue(Long.class));
            switch (checkedRule.getFrameVal()){
                case DAY:
                    if(Utils.compareDates(useTime, today)){
                        secsUsed += 10;
                    }
                    break;
                case WEEK:
                    if(useTime.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)
                            && useTime.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
                        secsUsed += 10;
                    }
                    break;
            }
        }

        if (checkedRule.isViolated(secsUsed)){
            notify("you have surpassed the amount defined!");
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "onCancelled", databaseError.toException());
    }

    private void notify(String description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckRunningApp.this)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                notificationDelay = 60;
            }
        })
            .setNegativeButton("NO, continue blocking", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
            .setIcon(R.drawable.phone_time_icon)
            .setTitle("STOP USING THIS APP")
            .setMessage(description)
            .setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("STOP USING THIS APP")
                .setContentText(description)
                .setSmallIcon(R.drawable.phone_time_icon);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());
        notificationDelay = 6; // set delay to 6*10 seconds = 1 minute
        */
    }
}
