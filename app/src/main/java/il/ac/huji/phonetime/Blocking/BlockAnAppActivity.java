package il.ac.huji.phonetime.blocking;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import il.ac.huji.phonetime.FirebaseManager;
import il.ac.huji.phonetime.MainActivity;
import il.ac.huji.phonetime.R;
import il.ac.huji.phonetime.Utils;

public class BlockAnAppActivity extends AppCompatActivity {

    private static final String TAG = BlockAnAppActivity.class.getSimpleName();
    // <editor-fold desc="view widgets">
    private RadioGroup radioGroup;
    private EditText timeAmount;
    private Spinner timeFrameSpinner;
    private Spinner timeUnitsSpinner;
    private EditText startHours;
    private EditText startMins;
    private EditText endHours;
    private EditText endMins;
    private ListView appsListView;
    // </editor-fold>
    private View prevSelectedView;
    private ListItem prevSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_an_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setComponents();
        setConfirm();
        if (!setContents(getIntent())){
            setRadio(R.id.rb_after);
            setList();
        }
    }

    private boolean setContents(Intent intent){
        if (null == intent) return false;
        Bundle extras = intent.getExtras();
        if (null == extras) return false;
        if (extras.containsKey("EXTRA_RULE") && extras.containsKey("EXTRA_PKG_NAME")) {
            Rule rule = (Rule) extras.getSerializable("EXTRA_RULE");
            if (rule instanceof RuleAfter){
                setRadio(R.id.rb_after);
                RuleAfter ruleAfter = (RuleAfter) rule;
                timeAmount.setText(Integer.toString(ruleAfter.getTime()));
                timeUnitsSpinner.setSelection(ruleAfter.getUnitsVal().ordinal());
                timeFrameSpinner.setSelection(ruleAfter.getFrameVal().ordinal());
            }else if (rule instanceof  RuleBetween){
                setRadio(R.id.rb_between);
                RuleBetween ruleBetween = (RuleBetween) rule;
                startHours.setText(getTimeString(ruleBetween.getFromHours()));
                startMins.setText(getTimeString(ruleBetween.getFromMinutes()));
                endHours.setText(getTimeString(ruleBetween.getToHours()));
                endMins.setText(getTimeString(ruleBetween.getToMinutes()));
            }

            String pkgName = extras.getString("EXTRA_PKG_NAME");
            try {
                ArrayList<ListItem> list = new ArrayList<>();
                String appName = Utils.getAppName(this, pkgName);
                Drawable appIcon = Utils.getAppIcon(this, pkgName);
                list.add(new ListItem(pkgName, appName, appIcon));
                appsListView.setAdapter(new ListAdapter(this, R.layout.item_choose_app,
                        R.id.txtAppName, R.id.radio_btn, R.id.app_logo_choose, list));

            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
                setList();
            }
            return true;
        }else{
            return false;
        }
    }

    private String getTimeString(int time){
        if (time < 0)
            return "00";
        if (time < 10)
            return "0" + time;
        return "" + time;
    }

    private void setConfirm(){
        FloatingActionButton okButton = (FloatingActionButton) findViewById(R.id.confirm);
        if (okButton != null) {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null == prevSelectedItem){
                        Toast.makeText(getApplicationContext(), "Please select an app from the list", Toast.LENGTH_LONG).show();
                    }
                    try{
                        Rule ruleType;
                        switch (radioGroup.getCheckedRadioButtonId()){
                            case R.id.rb_after:
                                ruleType = new RuleAfter(Integer.parseInt(timeAmount.getText().toString()),
                                        timeUnitsSpinner.getSelectedItemPosition() == 0 ?
                                                RuleAfter.TimeUnits.MINS : RuleAfter.TimeUnits.HOURS,
                                        timeFrameSpinner.getSelectedItemPosition() == 0 ?
                                                MainActivity.TimeFrame.DAY : MainActivity.TimeFrame.WEEK);
                                FirebaseManager.addRule(prevSelectedItem.getPackageName().replace('.', '-'), ruleType);
                                break;
                            default:
                                ruleType = new RuleBetween(Integer.parseInt(startHours.getText().toString()),
                                        Integer.parseInt(startMins.getText().toString()),
                                        Integer.parseInt(endHours.getText().toString()),
                                        Integer.parseInt(endMins.getText().toString()));
                                FirebaseManager.addRule(prevSelectedItem.getPackageName().replace('.', '-'), ruleType);
                                break;
                        }

                        Toast.makeText(getApplicationContext(), "Your rule was saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (NumberFormatException e){
                        Toast.makeText(getApplicationContext(), "Please fill all text fields", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void setComponents(){
        radioGroup =        (RadioGroup)findViewById(R.id.rb_group);
        timeAmount =        (EditText)  findViewById(R.id.time_amount);
        timeFrameSpinner =  (Spinner)   findViewById(R.id.timeFrameSpinner);
        timeUnitsSpinner =  (Spinner)   findViewById(R.id.timeUnitsSpinner);
        startHours  =       (EditText)  findViewById(R.id.start_hours);
        startMins =         (EditText)  findViewById(R.id.start_min);
        endHours =          (EditText)  findViewById(R.id.end_hours);
        endMins =           (EditText)  findViewById(R.id.end_min);
        appsListView =          (ListView)  findViewById(R.id.app_to_block);
    }

    private void setRadio(int checkedId){
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    boolean cond = (checkedId == R.id.rb_after);
                    timeAmount.setEnabled(cond);
                    timeFrameSpinner.setEnabled(cond);
                    timeUnitsSpinner.setEnabled(cond);
                    startHours.setEnabled(!cond);
                    startMins.setEnabled(!cond);
                    endHours.setEnabled(!cond);
                    endMins.setEnabled(!cond);
                }
            });
            radioGroup.check(checkedId);//R.id.rb_after
        }
    }

    private void setList() {
        // get list of all installed apps
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities( mainIntent, 0);
        // filter system apps
        Iterator<ResolveInfo> i = pkgAppsList.iterator();
        while (i.hasNext()) {
            ResolveInfo s = i.next();
            if ((s.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                i.remove();
            }
        }
        // set am empty adapter
        ListAdapter adapter = new ListAdapter(this, R.layout.item_choose_app,
                R.id.txtAppName, R.id.radio_btn, R.id.app_logo_choose, new ArrayList<ListItem>());
        appsListView.setAdapter(adapter);
        // load the list of apps (with icons) to the adapter
        new ListLoader(adapter).execute(pkgAppsList.toArray(new ResolveInfo[pkgAppsList.size()]));
        // set radio button behaviour to the list items
        appsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(prevSelectedView != null){
                    ImageView prevImage = ((ListAdapter.ViewHolder) prevSelectedView.getTag()).radioView;
                    prevImage.setImageResource(android.R.drawable.radiobutton_off_background);
                    prevSelectedItem.setSelected(false);
                }
                ImageView image = ((ListAdapter.ViewHolder) view.getTag()).radioView;
                image.setImageResource(android.R.drawable.radiobutton_on_background);
                prevSelectedView = view;
                prevSelectedItem = (ListItem) appsListView.getAdapter().getItem(position);
                prevSelectedItem.setSelected(true);
                Log.d("LIST VIEW", "Selected: " + prevSelectedItem.getAppName());
            }
        });
    }

    private class ListAdapter extends ArrayAdapter<ListItem> {

        private class ViewHolder{
            TextView nameView;
            ImageView iconView;
            ImageView radioView;
        }

        private int itemLayoutId;
        private int nameViewId;
        private int iconViewId;
        private int radioViewId;


        public ListAdapter(Context context, int resource, int nameViewResourceId,
                           int checkedViewResourceId, int iconViewResourceId, List<ListItem> apps) {
            super(context, resource, apps);
            itemLayoutId = resource;
            nameViewId = nameViewResourceId;
            iconViewId = iconViewResourceId;
            radioViewId = checkedViewResourceId;
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            ListItem item =  getItem(position);
            ViewHolder viewHolder;
            if (null == itemView){
                viewHolder = new ViewHolder();
                itemView = LayoutInflater.from(getContext()).inflate(itemLayoutId, parent, false);
                viewHolder.nameView = (TextView) itemView.findViewById(nameViewId);
                viewHolder.iconView = (ImageView) itemView.findViewById(iconViewId);
                viewHolder.radioView = (ImageView) itemView.findViewById(radioViewId);
                itemView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) itemView.getTag();
            }

            viewHolder.nameView.setText(item.getAppName());
            viewHolder.iconView.setImageDrawable(item.getAppIcon());
            viewHolder.radioView.setImageResource(item.getSelectionResId());
            return itemView;
        }
    }

    private class ListItem{
        private String mPackageName;
        private String mName;
        private Drawable mIcon;
        private int selectionResId;

        ListItem(String packageName, String appName, Drawable appIcon){
            mName = appName;
            mIcon = appIcon;
            mPackageName = packageName;
            selectionResId = android.R.drawable.radiobutton_off_background;
        }

        // <editor-fold desc="getters">
        public String getAppName() {
            return mName;
        }

        public Drawable getAppIcon() {
            return mIcon;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public int getSelectionResId(){
            return selectionResId;
        }
        // </editor-fold>

        public void setSelected(boolean selected){
            selectionResId = selected ? android.R.drawable.radiobutton_on_background
                    : android.R.drawable.radiobutton_off_background;
        }
    }

    private class ListLoader extends AsyncTask<ResolveInfo, ListItem, Void> {

        private final WeakReference<ListAdapter> mAdapter;
        private final PackageManager pm = getPackageManager();

        public ListLoader(ListAdapter adapter) {
            // Use a WeakReference to ensure the ListAdapter can be garbage collected
            mAdapter = new WeakReference<>(adapter);
        }

        @Override
        protected Void doInBackground(ResolveInfo... pkgAppsList) {
            for (ResolveInfo resInfo : pkgAppsList){
                ApplicationInfo ai = resInfo.activityInfo.applicationInfo;
                ListItem item = new ListItem(ai.packageName, (String) pm.getApplicationLabel(ai), pm.getApplicationIcon(ai));
                publishProgress(item);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ListItem... items) {
            super.onProgressUpdate(items);
            mAdapter.get().add(items[0]);
        }
    }
}
