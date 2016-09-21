package il.ac.huji.phonetime.blocking;

import com.google.firebase.database.Exclude;

public class RuleAfter implements Rule{
    public enum TimeFrame {DAY, WEEK}
    public enum TimeUnits {MINS, HOURS}

    private int time;
    private TimeUnits units;
    private TimeFrame frame;

    public RuleAfter() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RuleAfter(int time, TimeUnits units, TimeFrame frame) {
        this.time = time;
        this.units = units;
        this.frame = frame;
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

    // The Firebase data mapper will ignore this
    @Exclude
    public TimeFrame getFrameVal() {
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
}
