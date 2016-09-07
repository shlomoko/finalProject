package il.ac.huji.phonetime;

public class ListItem {
    private String name;
    private int time;

    ListItem(String appName, int timeUsed){
        name = appName;
        time = timeUsed;
    }

    public String getAppName(){
        return name;
    }

    public int getTimeUsed(){
        return time;
    }
}
