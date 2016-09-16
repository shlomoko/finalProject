package il.ac.huji.phonetime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BlockAnAppActivity extends AppCompatActivity {

    private EditText timeAmount;
    private Spinner timeFrameSpinner;
    private Spinner timeUnitsSpinner;
    private EditText startTime;
    private EditText endTime;
    private ListView appsList;
    private View prevSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_an_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setComponents();
        setConfirm();
        setRadio();
        setList();
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
        timeAmount =        (EditText) findViewById(R.id.time_amount);
        timeFrameSpinner =  (Spinner)  findViewById(R.id.timeFrameSpinner);
        timeUnitsSpinner =  (Spinner)  findViewById(R.id.timeUnitsSpinner);
        startTime =         (EditText) findViewById(R.id.start_time);
        endTime =           (EditText) findViewById(R.id.end_time);
        appsList =          (ListView) findViewById(R.id.app_to_block);
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

    private void setList() {
        prevSelected = null;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities( mainIntent, 0);
        List<ListItem> list = new ArrayList<>();
        for (ResolveInfo resInfo : pkgAppsList){
            ApplicationInfo ai = resInfo.activityInfo.applicationInfo;
            list.add(new ListItem((String) pm.getApplicationLabel(ai), false, pm.getApplicationIcon(ai)));
        }
        appsList.setAdapter(new ListAdapter(this, R.layout.item_choose_app,
                R.id.txtAppName, R.id.radio_btn, R.id.app_logo_choose, list));
        appsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(prevSelected != null){
                    ImageView prevImage = (ImageView)prevSelected.findViewById(R.id.radio_btn);
                    prevImage.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                ImageView image = (ImageView)view.findViewById(R.id.radio_btn);
                image.setImageResource(android.R.drawable.radiobutton_on_background);
                prevSelected = view;
            }
        });
    }

    private class ListAdapter extends ArrayAdapter<ListItem> {

        private int itemLayoutId;
        private int nameViewId;
        private int iconViewId;
        private int radioViewId;
        private Context context;
        List<ListItem> list;

        public ListAdapter(Context context, int resource, int nameViewResourceId,
                           int checkedViewResourceId, int iconViewResourceId, List<ListItem> apps) {
            super(context, resource, nameViewResourceId, apps);
            this.context = context;
            this.itemLayoutId = resource;
            nameViewId = nameViewResourceId;
            iconViewId = iconViewResourceId;
            radioViewId = checkedViewResourceId;
            list = apps;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = super.getView(position, convertView, parent);
            if (null == itemView){
                itemView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
            }
            TextView nameView = (TextView) itemView.findViewById(nameViewId);
            ImageView iconView = (ImageView) itemView.findViewById(iconViewId);
            ImageView radioView = (ImageView) itemView.findViewById(radioViewId);

            ListItem item = list.get(position);
            nameView.setText(item.getAppName());
            iconView.setImageDrawable(item.getAppIcon());
            radioView.setImageResource(item.getIsChecked() ?
                    android.R.drawable.radiobutton_on_background :
                    android.R.drawable.radiobutton_off_background);

            return itemView;
        }
    }

    private class ListItem{
        private String name;
        private Drawable icon;
        private Boolean checked;

        ListItem(String appName, boolean isChecked, Drawable appIcon){
            name = appName;
            checked = isChecked;
            icon = appIcon;
        }

        public String getAppName() {
            return name;
        }

        public Drawable getAppIcon() {
            return icon;
        }

        public Boolean getIsChecked() {
            return checked;
        }

        public void setIsChecked(Boolean checked) {
            this.checked = checked;
        }
    }
}
