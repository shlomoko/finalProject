package il.ac.huji.phonetime;

import android.content.ContentResolver;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import il.ac.huji.phonetime.blocking.Rule;

public class FirebaseManager {

    private static String USES = "uses";
    private static String RULES = "rules";

    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static String phoneIdHashed;

    public static void init(Context context, ContentResolver contentRes){
        phoneIdHashed = getDeviceId(context, contentRes);
    }

    public static void addUse(Use use){
        mRootRef.child(phoneIdHashed).child(USES).push().setValue(use);
    }
    
    public static void addBetweenRule(String pkgName, Rule rule){
        mRootRef.child(phoneIdHashed).child("rules").child("between").child(pkgName).setValue(rule);
    }

    public static void addAfterRule(String pkgName, Rule rule) {
        mRootRef.child(phoneIdHashed).child("rules").child("after").child(pkgName).setValue(rule);
    }

    public static void getUsesList(ValueEventListener listener){
        mRootRef.child(phoneIdHashed).child(USES).addListenerForSingleValueEvent(listener);
    }

    public static void getRulesChanges(ChildEventListener listener){
        mRootRef.child(phoneIdHashed).child(RULES).addChildEventListener(listener);
    }

    public static void deleteRule(String pkgName){
        mRootRef.child(phoneIdHashed).child(RULES).child(pkgName).removeValue();
    }

    public static void getAfterList(ValueEventListener listener){
        mRootRef.child(phoneIdHashed).child("rules").child("after").addListenerForSingleValueEvent(listener);
    }

    public static void getBetweenList(ValueEventListener listener){
        mRootRef.child(phoneIdHashed).child("rules").child("between").addListenerForSingleValueEvent(listener);
    }

    private static String getDeviceId(Context context, ContentResolver contentRes){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(contentRes, android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}
