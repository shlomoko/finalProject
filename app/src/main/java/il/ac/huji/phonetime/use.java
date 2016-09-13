package il.ac.huji.phonetime;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Shlomo on 05/09/2016.
 */
@IgnoreExtraProperties
public class use {
    public String packageName;
    public long timeStamp;

    public use() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public use(String packageName, long timeStamp) {
        this.packageName = packageName;
        this.timeStamp = timeStamp;
    }
}
