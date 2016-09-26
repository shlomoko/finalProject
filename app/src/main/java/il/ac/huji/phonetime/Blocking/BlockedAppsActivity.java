package il.ac.huji.phonetime.blocking;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.phonetime.FirebaseManager;
import il.ac.huji.phonetime.R;
import il.ac.huji.phonetime.Utils;

public class BlockedAppsActivity extends AppCompatActivity implements ChildEventListener {

    private static final String TAG = BlockedAppsActivity.class.getSimpleName();
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = (ListView) findViewById(R.id.blockedList);
        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        mAdapter = new ListAdapter(this, R.layout.item_blocked_app, R.id.app_name,
                R.id.app_logo_blocked, new ArrayList<ListItem>());
        if (lv != null) {
            lv.setEmptyView(emptyText);
            lv.setAdapter(mAdapter);
        }
        FirebaseManager.getRulesChanges(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BlockedAppsActivity.this, BlockAnAppActivity.class);
                    startActivity(intent);
                }
            });
        }
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onChildAdded(DataSnapshot ruleSnapshot, String s) {
        String pkgName = ruleSnapshot.getKey().replace('-', '.');
        String appName;
        Drawable appIcon;
        try {
            appName = Utils.getAppName(getApplicationContext(), pkgName);
            appIcon = Utils.getAppIcon(getApplicationContext(), pkgName);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getLocalizedMessage());
            return;
        }

        mAdapter.add(new ListItem(pkgName, appName, appIcon, Utils.getRuleObj(ruleSnapshot)));
    }

    @Override
    public void onChildChanged(DataSnapshot ruleSnapshot, String s) {
        String pkgName = ruleSnapshot.getKey().replace('-', '.');
        mAdapter.update(pkgName, Utils.getRuleObj(ruleSnapshot));
    }

    @Override
    public void onChildRemoved(DataSnapshot ruleSnapshot) {
        String pkgName = ruleSnapshot.getKey().replace('-', '.');
        mAdapter.remove(pkgName);
    }

    @Override
    public void onChildMoved(DataSnapshot ruleSnapshot, String s) { }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Firebase", "onCancelled", databaseError.toException());
    }

    private class ListAdapter extends ArrayAdapter<ListItem> {

        private int itemLayoutId;
        private int nameViewId;
        private int iconViewId;
        private Context context;
        private List<ListItem> items;

        public ListAdapter(Context context, int resource, int nameViewResourceId,
                           int iconViewResourceId, List<ListItem> apps) {
            super(context, resource, nameViewResourceId, apps);
            this.context = context;
            this.itemLayoutId = resource;
            nameViewId = nameViewResourceId;
            iconViewId = iconViewResourceId;
            items = apps;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = super.getView(position, convertView, parent);
            if (null == itemView){
                itemView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
            }
            TextView nameView = (TextView) itemView.findViewById(nameViewId);
            ImageView iconView = (ImageView) itemView.findViewById(iconViewId);

            final ListItem item = items.get(position);
            nameView.setText(item.getAppName());
            iconView.setImageDrawable(item.getIcon());


            ImageButton deleteBtn = (ImageButton)itemView.findViewById(R.id.delete_rule);
            ImageButton editBtn = (ImageButton)itemView.findViewById(R.id.edit_rule);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    FirebaseManager.deleteRule(item.getPkgName().replace('.', '-'));
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BlockedAppsActivity.this, BlockAnAppActivity.class);
                    intent.putExtra("EXTRA_RULE", item.getRule());
                    intent.putExtra("EXTRA_PKG_NAME", item.getPkgName());
                    startActivity(intent);
                }
            });


            return itemView;
        }

        public void update(String pkgName, Rule newRule){
            for(ListItem item : items){
                if (pkgName.equals(item.getPkgName())){
                    item.setRule(newRule);
                    return;
                }
            }
        }

        public void remove(String pkgName){
            for(ListItem item : items){
                if (pkgName.equals(item.getPkgName())){
                    remove(item);
                    return;
                }
            }
        }
    }

    private class ListItem{
        private String appLabel;
        private String pkgName;
        private Drawable icon;
        private Rule rule;

        ListItem(String packageName, String appName, Drawable appIcon, Rule appRule){
            appLabel = appName;
            icon = appIcon;
            rule = appRule;
            pkgName = packageName;
        }

        public String getAppName(){
            return appLabel;
        }

        public Drawable getIcon() {
            return icon;
        }

        public Rule getRule() {
            return rule;
        }

        public void setRule(Rule newRule){
            rule = newRule;
        }

        public String getPkgName() {
            return pkgName;
        }
    }
}
