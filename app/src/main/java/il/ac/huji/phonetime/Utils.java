package il.ac.huji.phonetime;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;

import il.ac.huji.phonetime.blocking.Rule;
import il.ac.huji.phonetime.blocking.RuleAfter;
import il.ac.huji.phonetime.blocking.RuleBetween;

public class Utils {
    public static int sumArray(int[] a){
        int sum = 0;
        for (int num : a) sum += num;
        return sum;
    }

    public static String getAppName(Context context, String pkgName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = pm.getApplicationInfo(pkgName, 0);
        return (String) pm.getApplicationLabel(ai);
    }

    public static Drawable getAppIcon(Context context, String pkgName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = pm.getApplicationInfo(pkgName, 0);
        return pm.getApplicationIcon(ai);
    }

    public static String getTimeString(Resources res, int time){
        if (time < 60)
            return res.getString(R.string.sec_used, time);
        return res.getString(R.string.min_used, time/60);
    }

    public static Rule getRuleObj(DataSnapshot ruleSnapshot){
        if (ruleSnapshot.hasChild("fromHours")){
            return ruleSnapshot.getValue(RuleBetween.class);
        }
        return ruleSnapshot.getValue(RuleAfter.class);
    }

    public static boolean compareDates(Calendar a, Calendar b){
        return a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH)
                && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
    }
}
