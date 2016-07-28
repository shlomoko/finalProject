package il.ac.huji.phonetime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PieChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PieChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartFragment extends Fragment {
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String APP_TIMES = "appTimes";
    public static final String TOTAL_TIME = "totalTime";

    private Map<String, int[]> mAppTimes;
    private int mTotalTime;

    private OnFragmentInteractionListener mListener;

    public PieChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param appTimes Parameter 1.
     * @param totalTime Parameter 2.
     * @return A new instance of fragment PieChartFragment.
     */
    public static PieChartFragment newInstance(HashMap<String, int[]> appTimes, int totalTime) {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(APP_TIMES, appTimes);
        args.putInt(TOTAL_TIME, totalTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppTimes = (Map<String, int[]>) getArguments().getSerializable(APP_TIMES);
            mTotalTime = getArguments().getInt(TOTAL_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        PieChart pie = (PieChart) v.findViewById(R.id.chart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : mAppTimes.entrySet()) {
            int sum = sumArray(entry.getValue());
            entries.add(new PieEntry((float)sum/mTotalTime, entry.getKey()));
        }
        PieDataSet dataset = new PieDataSet(entries, "label woohoo");
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataset);
        pie.setData(data);
        pie.setDescription("");
        pie.setDrawHoleEnabled(false);

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
}
