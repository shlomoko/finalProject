package il.ac.huji.phonetime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendsFragment extends Fragment {
    public static final String APP_TIMES = "appTimes";

    private Map<String, int[]> mAppTimes;

    private OnFragmentInteractionListener mListener;

    public TrendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param appTimes Parameter 1.
     * @return A new instance of fragment TrendsFragment.
     */
    public static TrendsFragment newInstance(HashMap<String, int[]> appTimes) {
        TrendsFragment fragment = new TrendsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trends, container, false);

        LineChart graph = (LineChart) v.findViewById(R.id.line_chart);

        List<ILineDataSet> dataSets = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : mAppTimes.entrySet()){
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < entry.getValue().length; i++){
                entries.add(new Entry(i, entry.getValue()[i]));
            }
            LineDataSet dataSet = new LineDataSet(entries, entry.getKey());
            dataSet.setDrawValues(false);
            dataSets.add(dataSet);
        }

        LineData data = new LineData(dataSets);
        graph.setData(data);
        graph.setDescription("");

        graph.getAxisRight().setEnabled(false);
        graph.getAxisLeft().setSpaceBottom(0);
        graph.getAxisLeft().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ((int) value) + " min.";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.getXAxis().setDrawGridLines(false);

        return v;
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
