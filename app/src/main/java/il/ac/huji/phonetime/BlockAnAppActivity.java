package il.ac.huji.phonetime;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class BlockAnAppActivity extends AppCompatActivity {

    private EditText timeAmount;
    private Spinner timeFrameSpinner;
    private Spinner timeUnitsSpinner;
    private EditText startTime;
    private EditText endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_an_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setComponents();
        setConfirm();
        setRadio();
    }

    private void setConfirm(){
        FloatingActionButton okButton = (FloatingActionButton) findViewById(R.id.confirm);
        if (okButton != null) {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO save to DB
                    Toast.makeText(getApplicationContext(), "Your rule was saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void setComponents(){
        timeAmount = (EditText)findViewById(R.id.time_amount);
        timeFrameSpinner = (Spinner)findViewById(R.id.timeFrameSpinner);
        timeUnitsSpinner = (Spinner)findViewById(R.id.timeUnitsSpinner);
        startTime = (EditText)findViewById(R.id.start_time);
        endTime = (EditText)findViewById(R.id.end_time);
    }


    private void setRadio(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rb_group);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    boolean cond = (checkedId == R.id.rb_after);
                    timeAmount.setEnabled(cond);
                    timeFrameSpinner.setEnabled(cond);
                    timeUnitsSpinner.setEnabled(cond);
                    startTime.setEnabled(!cond);
                    endTime.setEnabled(!cond);
                }
            });
            radioGroup.check(R.id.rb_after);
        }
    }
}
