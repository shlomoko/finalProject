package il.ac.huji.phonetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ListItem>{

    private int resource;
    private int nameViewId;
    private int timeViewId;
    private Context context;
    List<ListItem> list;

    public ListAdapter(Context context, int resource, int nameViewResourceId,
                       int timeViewResourceId, List<ListItem> apps) {
        super(context, resource, nameViewResourceId, apps);
        this.context = context;
        this.resource = resource;
        nameViewId = nameViewResourceId;
        timeViewId = timeViewResourceId;
        list = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);
        if (null == itemView){
            itemView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView nameView = (TextView) itemView.findViewById(nameViewId);
        TextView timeView = (TextView) itemView.findViewById(timeViewId);

        ListItem item = list.get(position);
        nameView.setText(item.getAppName());
        timeView.setText(item.getTimeUsed() + "min.");

        return itemView;
    }
}
