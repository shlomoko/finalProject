package il.ac.huji.phonetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Shlomo on 28/07/2016.
 */
public class AlarmRec extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
//    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CheckRunningApp.class);
        i.putExtra("foo", "bar");
//        if (intent.getAction().equals(intent.ACTION_SCREEN_ON)) {
            context.startService(i);
//        }
    }
}