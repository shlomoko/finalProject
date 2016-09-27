package il.ac.huji.phonetime.blocking;

import java.io.Serializable;

public interface Rule extends Serializable {
    boolean isViolated(int... params);
}
