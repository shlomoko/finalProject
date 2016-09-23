package il.ac.huji.phonetime;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.phonetime.blocking.Rule;
import il.ac.huji.phonetime.blocking.RuleAfter;
import il.ac.huji.phonetime.blocking.RuleBetween;

/**
 * Created by Shlomo on 22/09/2016.
 */
public abstract class BaseActivity extends Activity implements ValueEventListener {
    private static int sessionDepth = 0;
    public static List<Rule> blockedApps= new ArrayList<Rule>();
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
            FirebaseManager.getAfterList(this);
            FirebaseManager.getBetweenList(this);
        }
    }

    public void onDataChange(DataSnapshot snapshot) {
        for (DataSnapshot blockedSnapshot : snapshot.getChildren()) {
            if (blockedSnapshot.hasChild("time")) {
                RuleAfter ruleAfter = blockedSnapshot.getValue(RuleAfter.class);
                blockedApps.add(ruleAfter);
            } else {
                RuleBetween ruleBetween  =blockedSnapshot.getValue(RuleBetween.class);
                blockedApps.add(ruleBetween);
            }
        }
    }
}
