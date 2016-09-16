package il.ac.huji.phonetime;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    public static final String APP_TIMES = "appTimes";
    private static final String TAG = "ListFragment";

    private Map<String, int[]> mAppTimes;
    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param appTimes Parameter 1.
     * @return A new instance of fragment ListFragment.
     */
    public static ListFragment newInstance(HashMap<String, int[]> appTimes) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(APP_TIMES, appTimes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppTimes = (Map<String, int[]>) getArguments().getSerializable(APP_TIMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ListView listView = (ListView) v.findViewById(R.id.list);
        List<ListItem> list = new ArrayList<>();
        ApplicationInfo ai;
        PackageManager pm = getActivity().getApplicationContext().getPackageManager();
        for (Map.Entry<String, int[]> entry : mAppTimes.entrySet()){
            try {
                ai = pm.getApplicationInfo(entry.getKey(), 0);
                list.add(new ListItem((String) pm.getApplicationLabel(ai),
                        sumArray(entry.getValue()), pm.getApplicationIcon(ai)));
            } catch (final PackageManager.NameNotFoundException e) {
                Log.d(TAG, "onCreateView: packageName unknown");
            }
        }
        Collections.sort(list);
        listView.setAdapter(new ListAdapter(getContext(), R.layout.item_list, R.id.txtAppName, R.id.txtTimeUsed, R.id.app_logo, list));
        return v;
    }

    private static int sumArray(int[] a){
        int sum = 0;
        for (int num : a) sum += num;
        return sum;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ListAdapter extends ArrayAdapter<ListItem> {

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
            timeView.setText(getResources().getString(R.string.min_used, item.getTimeUsed()));
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
