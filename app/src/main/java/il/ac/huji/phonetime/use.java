package il.ac.huji.phonetime;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class use {
    private String packageName;
    private long timeStamp;

    public use() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public use(String packageName, long timeStamp) {
        this.packageName = packageName;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getPackageName() {
        return packageName;
    }
}
