package il.ac.huji.phonetime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements PieChartFragment.OnFragmentInteractionListener, TrendsFragment.OnFragmentInteractionListener{

    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment(savedInstanceState);
        setBlockButton();
        setToggels();
    }

    // https://developer.android.com/training/basics/fragments/fragment-ui.html
    private void setFragment(Bundle savedInstanceState){
        // Check that the activity is using the layout version with the fragment_container
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state, then we don't need to do
            // anything and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            PieChartFragment firstFragment = new PieChartFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            HashMap<String, int[]> map = new HashMap<>();
            map.put("Facebbok", new int[]{2,0,5,3});
            map.put("Whatsapp", new int[]{4,1,0,2});
            map.put("Calendar", new int[]{8,2,0,1});
            map.put("Chrome", new int[]{2,5,0,1});

            args.putSerializable(PieChartFragment.APP_TIMES, map);
            firstFragment.setArguments(args);


            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    private void setBlockButton(){
        Button but = (Button) findViewById(R.id.blockButton);
        if (but != null) {
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BlockedAppsActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    private void setToggels(){
        RadioGroup group = (RadioGroup)findViewById(R.id.typesGroup);
        if (group != null) {
            group.setOnCheckedChangeListener(ToggleListener);
        }
        group = (RadioGroup)findViewById(R.id.timeFrameGroup);
        if (group != null) {
            group.setOnCheckedChangeListener(ToggleListener);
        }
    }

    public void onTypeToggle(View view){
        int checkedId = view.getId();
        ((RadioGroup)view.getParent()).check(checkedId);
        //replace fragment
        Fragment newFrag;
        switch (checkedId){
            case R.id.btn_pie:
                newFrag = new PieChartFragment();
                newFrag.setArguments(args);
                break;
            case R.id.btn_list:
                newFrag = null;//TODO
                break;
            case R.id.btn_trends:
                newFrag = new TrendsFragment();
                newFrag.setArguments(args);
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newFrag).commit();
    }

    public void onTimeFrameToggle(View view){
        int checkedId = view.getId();
        ((RadioGroup)view.getParent()).check(checkedId);
        //TODO send this value to current fragment
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
