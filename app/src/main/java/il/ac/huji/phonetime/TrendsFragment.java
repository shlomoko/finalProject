package il.ac.huji.phonetime;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendsFragment extends StatsFragment{

    private static final String TAG = TrendsFragment.class.getSimpleName();

    public TrendsFragment() {
        // Required empty public constructor
        super(R.layout.fragment_trends);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrendsFragment.
     */
    public static TrendsFragment newInstance() {
        return new TrendsFragment();
    }

    @Override
    protected void setContent() {
        List<ILineDataSet> dataSets = new ArrayList<>();
        int numOfColors = ColorTemplate.JOYFUL_COLORS.length;
        int color = 0;
        for (Map.Entry<String, int[]> entry : mData.entrySet()){
            try {
                String appName = Utils.getAppName(getActivity().getApplicationContext(), entry.getKey());
                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < entry.getValue().length; i++){
                    entries.add(new Entry(i, entry.getValue()[i]));
                }
                LineDataSet dataSet = new LineDataSet(entries, appName);
                dataSet.setDrawValues(false);
                dataSet.setColors(new int[]{ColorTemplate.JOYFUL_COLORS[color % numOfColors]});
                dataSets.add(dataSet);
                color++;
            } catch (PackageManager.NameNotFoundException e){
                Log.d(TAG, e.getLocalizedMessage(), e);
            }
        }

        LineData data = new LineData(dataSets);

        LineChart graphView = (LineChart) mDataView;
        graphView.setData(data);
        graphView.setDescription("");

        graphView.getAxisRight().setEnabled(false);
        graphView.getAxisLeft().setSpaceBottom(0);
        graphView.getAxisLeft().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Utils.getTimeString(getResources(), (int) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        graphView.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graphView.getXAxis().setDrawGridLines(false);

        graphView.setVisibility(View.VISIBLE);

        if(dataSets.isEmpty()) mEmptyText.setText(R.string.no_data);
        else mEmptyText.setVisibility(View.INVISIBLE);
    }
}
