package il.ac.huji.phonetime;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends StatsFragment{

    private static final String TAG = ListFragment.class.getSimpleName();
    private ListAdapter mAdapter;

    public ListFragment() {
        // Required empty public constructor
        super(R.layout.fragment_list);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListFragment.
     */
    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new ListAdapter(getContext(), R.layout.item_list, R.id.txtAppName,
                R.id.txtTimeUsed, R.id.app_logo, new ArrayList<ListItem>());
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ((ListView) mDataView).setEmptyView(mEmptyText);
        ((ListView) mDataView).setAdapter(mAdapter);
        return v;
    }

    @Override
    protected void setContent(){
        mAdapter.clear();
        ArrayList<ListItem> list = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : mData.entrySet()){
            try {
                String appName = Utils.getAppName(getActivity().getApplicationContext(), entry.getKey());
                Drawable appIcon = Utils.getAppIcon(getActivity().getApplicationContext(), entry.getKey());
                list.add(new ListItem(appName, Utils.sumArray(entry.getValue()), appIcon));
            } catch (final PackageManager.NameNotFoundException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        }
        Collections.sort(list, Collections.<ListItem>reverseOrder());
        mAdapter.addAll(list);
        if(list.isEmpty()) mEmptyText.setText(R.string.no_data);
    }

    @Override
    protected void showLoading() {
        if (null != mAdapter) {
            mAdapter.clear();
            mEmptyText.setText(getString(R.string.loading));
        }
    }

    private class ListAdapter extends ArrayAdapter<ListItem> {

        private int itemLayoutId;
        private int nameViewId;
        private int timeViewId;
        private int iconViewId;
        private Context context;
        List<ListItem> list;

        public ListAdapter(Context context, int resource, int nameViewResourceId,
                           int timeViewResourceId, int iconViewResourceId, List<ListItem> apps) {
            super(context, resource, nameViewResourceId, apps);
            this.context = context;
            this.itemLayoutId = resource;
            nameViewId = nameViewResourceId;
            timeViewId = timeViewResourceId;
            iconViewId = iconViewResourceId;
            list = apps;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = super.getView(position, convertView, parent);
            if (null == itemView){
                itemView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
            }
            TextView nameView = (TextView) itemView.findViewById(nameViewId);
            TextView timeView = (TextView) itemView.findViewById(timeViewId);
            ImageView iconView = (ImageView) itemView.findViewById(iconViewId);

            ListItem item = list.get(position);
            nameView.setText(item.getAppName());
            timeView.setText(Utils.getTimeString(getResources(), item.getTimeUsed()));
            iconView.setImageDrawable(item.getIcon());

            return itemView;
        }
    }

    private class ListItem implements Comparable<ListItem>{
        private String name;
        private int time;
        private Drawable icon;

        ListItem(String appName, int timeUsed, Drawable appIcon){
            name = appName;
            time = timeUsed;
            icon = appIcon;
        }

        public String getAppName(){
            return name;
        }

        public int getTimeUsed(){
            return time;
        }

        public Drawable getIcon() {
            return icon;
        }

        @Override
        public int compareTo(@NonNull ListItem another) {
            return time - another.time;
        }
    }
}
