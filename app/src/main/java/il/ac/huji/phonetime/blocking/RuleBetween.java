package il.ac.huji.phonetime.blocking;

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
}
