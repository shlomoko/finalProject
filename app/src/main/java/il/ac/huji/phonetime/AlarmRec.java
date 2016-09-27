package il.ac.huji.phonetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.view.Display;


public class AlarmRec extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkScreenOn(context)) {
            Intent i = new Intent(context, CheckRunningApp.class);
            i.putExtra("foo", "bar");
            context.startService(i);
        }
    }

    private boolean checkScreenOn(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    return true;
                }
            }
            return false;
        } else {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return powerManager.isScreenOn();
        }
    }
}