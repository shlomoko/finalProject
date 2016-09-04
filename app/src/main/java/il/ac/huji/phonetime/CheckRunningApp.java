package il.ac.huji.phonetime;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by Shlomo on 28/07/2016.
 */
public class CheckRunningApp extends IntentService {
    public CheckRunningApp() {
        super("CheckRunningApp");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context mContext = this; //##################### TODO NOT SURE ###########################
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
        Log.i("current_app",tasks.get(0).processName);
        Log.i("CheckRunningApp", "Service running");
    }
}
