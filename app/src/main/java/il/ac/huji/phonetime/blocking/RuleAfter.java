package il.ac.huji.phonetime.blocking;

import com.google.firebase.database.Exclude;

import il.ac.huji.phonetime.MainActivity;

public class RuleAfter implements Rule{
    private int time;
    private TimeUnits units;
    private MainActivity.TimeFrame frame;
    public RuleAfter() {
        // Default constructor required for calls to DataSnapshot.getValue(RuleAfter.class)
    }

    public RuleAfter(int time, TimeUnits units, MainActivity.TimeFrame frame) {
        this.time = time;
        this.units = units;
        this.frame = frame;
    }

    @Override
    public boolean isViolated(int... params) {
        int secsUsed = params[0];
        if (TimeUnits.MINS == units){
            return secsUsed / 60 >= time;
        }else if (TimeUnits.HOURS == units){
            return secsUsed / 3600 >= time;
        }
        return false;
    }

    public int getTime() {
        return time;
    }

    // The Firebase data mapper will ignore this
    @Exclude
    public TimeUnits getUnitsVal() {
        return units;
    }

    public String getUnits() {
        // Convert enum to string
        if (units == null) {
            return null;
        } else {
            return units.name();
        }
    }

    public void setUnits(String val){
        // Convert string to enum
        units = TimeUnits.valueOf(val);
    }

    // The Firebase data mapper will ignore this
    @Exclude
    public MainActivity.TimeFrame getFrameVal() {
        return frame;
    }

    public String getFrame() {
        // Convert enum to string
        if (frame == null) {
            return null;
        } else {
            return frame.name();
        }
    }

    public void setFrame(String val){
        frame = MainActivity.TimeFrame.valueOf(val);
    }

    public enum TimeUnits {MINS, HOURS}
}
