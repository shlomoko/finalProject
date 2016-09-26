package il.ac.huji.phonetime;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import il.ac.huji.phonetime.blocking.Rule;

public abstract class BaseActivity extends Activity implements ValueEventListener {
    private static int sessionDepth = 0;
    public static Map<String, Rule> blockedApps = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        sessionDepth++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {
            // app went to background
            FirebaseManager.getRulesList(this);
        }
    }

    public void onDataChange(DataSnapshot snapshot) {
        for (DataSnapshot blockedSnapshot : snapshot.getChildren()) {
            blockedApps.put(blockedSnapshot.getKey(), Utils.getRuleObj(blockedSnapshot));
        }
    }
}
