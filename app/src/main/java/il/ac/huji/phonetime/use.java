package il.ac.huji.phonetime;


public class Use {
    public String packageName;
    public long timeStamp;

    public Use() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Use(String packageName, long timeStamp) {
        this.packageName = packageName;
        this.timeStamp = timeStamp;
    }
}
