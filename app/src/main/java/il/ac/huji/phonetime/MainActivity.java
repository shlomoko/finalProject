package il.ac.huji.phonetime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity
        implements PieChart.OnFragmentInteractionListener, TrendsFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment(savedInstanceState);
        setBlockButton();
        setSpinners();
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
            PieChart firstFragment = new PieChart();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

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

    private void setSpinners(){
        Spinner dispTypeSpinner = (Spinner)findViewById(R.id.displayTypeSpinner);
        if (dispTypeSpinner != null) {
            dispTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //replace fragment
                    Fragment newFrag;// = null;
                    switch (position){
                        case 0: //pie
                            newFrag = new PieChart();
                            break;
                        case 1: //list
                            newFrag = null;//TODO
                            break;
                        case 2: //trends
                            newFrag = new TrendsFragment();
                            break;
                        default:
                            newFrag = null;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newFrag).commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }

        //TODO time-frame spinner listener: send params to current fragment
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
