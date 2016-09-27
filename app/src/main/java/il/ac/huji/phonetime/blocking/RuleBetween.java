package il.ac.huji.phonetime.blocking;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RuleBetween implements Rule{
    private int fromHours;
    private int fromMinutes;
    private int toHours;
    private int toMinutes;

    public RuleBetween() {
        // Default constructor required for calls to DataSnapshot.getValue(RuleBetween.class)
    }

    public RuleBetween(int fromHours, int fromMinutes, int toHours, int toMinutes) {
        this.fromHours = fromHours;
        this.fromMinutes = fromMinutes;
        this.toHours = toHours;
        this.toMinutes = toMinutes;
    }

    public int getFromHours() {
        return fromHours;
    }

    public int getFromMinutes() {
        return fromMinutes;
    }

    public int getToHours() {
        return toHours;
    }

    public int getToMinutes() {
        return toMinutes;
    }

    @Override
    public boolean isViolated(int... params) {
        Calendar now = GregorianCalendar.getInstance();
        Calendar from = new GregorianCalendar();
        from.set(Calendar.HOUR_OF_DAY, fromHours);
        from.set(Calendar.MINUTE, fromMinutes);
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, toHours);
        to.set(Calendar.MINUTE, toMinutes);
        return now.after(from) && now.before(to);
    }
}
