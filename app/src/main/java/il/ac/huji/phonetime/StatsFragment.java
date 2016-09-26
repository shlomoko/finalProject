package il.ac.huji.phonetime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public abstract class StatsFragment extends Fragment {
    private static final String TAG = StatsFragment.class.getSimpleName();

    protected Map<String, int[]> mData;
    protected TextView mEmptyText;
    protected View mDataView;
    private int mLayoutResId;

    protected StatsFragment(int layoutResId){
        mLayoutResId = layoutResId;
    }

    protected abstract void setContent();

    protected void showLoading(){
        if (null != mDataView) {
            mDataView.setVisibility(View.INVISIBLE);
            mEmptyText.setVisibility(View.VISIBLE);
            mEmptyText.setText(getString(R.string.loading));
        }
    }

    public void update(Map<String, int[]> newData) {
        mData = newData;
        if (null != mDataView) setContent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(mLayoutResId, container, false);

        mDataView = v.findViewById(R.id.dataView);
        mEmptyText = (TextView) v.findViewById(R.id.emptyText);
        showLoading();

        if (null != mData && mData.size() > 0) setContent();
        Log.d(TAG, "Fragment view loaded.");

        return v;
    }
}
