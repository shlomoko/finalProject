package il.ac.huji.phonetime.blocking;

public class RuleBetween implements Rule{
    private int fromHours;
    private int fromSeconds;
    private int toHours;
    private int toSeconds;

    public RuleBetween() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RuleBetween(int fromHours, int fromSeconds, int toHours, int toSeconds) {
        this.fromHours = fromHours;
        this.fromSeconds = fromSeconds;
        this.toHours = toHours;
        this.toSeconds = toSeconds;
    }

    public int getFromHours() {
        return fromHours;
    }

    public int getFromSeconds() {
        return fromSeconds;
    }

    public int getToHours() {
        return toHours;
    }

    public int getToSeconds() {
        return toSeconds;
    }
}
