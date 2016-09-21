package il.ac.huji.phonetime;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PieChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PieChartFragment extends StatsFragment {

    private static final String TAG = PieChartFragment.class.getSimpleName();

    public PieChartFragment() {
        // Required empty public constructor
        super(R.layout.fragment_pie_chart);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PieChartFragment.
     */
    public static PieChartFragment newInstance() {
        return new PieChartFragment();
    }

    @Override
    protected void setContent() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : mData.entrySet()) {
            try {
                String appName = Utils.getAppName(getActivity().getApplicationContext(), entry.getKey());
                entries.add(new PieEntry(Utils.sumArray(entry.getValue()), appName));
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            }

        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry e, int index, ViewPortHandler vph) {
                return Utils.getTimeString(getResources(), (int) value);
            }
        });

        PieChart pie = (PieChart) mDataView;
        pie.setData(data);
        pie.setDescription("");
        pie.setDrawHoleEnabled(false);
        pie.invalidate();
        pie.setVisibility(View.VISIBLE);
        if(entries.isEmpty()) mEmptyText.setText(R.string.no_data);
        else mEmptyText.setVisibility(View.INVISIBLE);
    }
}
