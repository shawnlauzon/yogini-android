package com.shantikama.yogini.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.PerformanceInfo;
import com.shantikama.yogini.R;

import java.util.List;

public class PerformanceListActivity extends NavigationDrawerActivity {
    private List<PerformanceInfo> mPerformances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawer(toolbar);

        setupListView();
    }

    private void setupListView() {
        mPerformances = JsonLibrary.getInstance().getPerformances(this);

        final ListView listView = (ListView) findViewById(android.R.id.list);
        assert listView != null;
        listView.setMultiChoiceModeListener(new PerformanceMultiChoiceModalListener(listView));
        listView.setAdapter(new PerformanceListViewAdapter(mPerformances));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PerformanceInfo performanceInfo = mPerformances.get(position);
                Intent intent = new Intent(PerformanceListActivity.this, PerformanceActivity.class);
                intent.putExtra(PerformanceActivity.ARG_PRACTICE_ID, performanceInfo.id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class PerformanceListViewAdapter extends ArrayAdapter<PerformanceInfo> {
        private final List<PerformanceInfo> mPerformances;

        public PerformanceListViewAdapter(List<PerformanceInfo> items) {
            super(PerformanceListActivity.this, R.layout.performance_list_content, items);
            mPerformances = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.performance_list_content, parent, false);
            }

            final PerformanceInfo performanceInfo = mPerformances.get(position);
            ((TextView) convertView.findViewById(R.id.name)).setText(performanceInfo.name);
            // TODO When I can auto-calculate, show how long is the practice
//                    ((TextView) convertView.findViewById(R.id.num_minutes)).setText(
//                            String.format(Locale.getDefault(), "%d", performanceInfo.timeMinutes));

            return convertView;
        }

        @Override
        public int getCount() {
            return mPerformances.size();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return mPerformances.get(position).getItemId();
        }
    }

    class PerformanceMultiChoiceModalListener implements AbsListView.MultiChoiceModeListener {

        private ListView mListView;

        PerformanceMultiChoiceModalListener(ListView listView) {
            this.mListView = listView;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mode.setTitle(String.valueOf(mListView.getCheckedItemCount()));
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.practice_list_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Respond to clicks on the actions in the CAB
            switch (item.getItemId()) {
                case R.id.action_delete:
                    confirmDelete(mode);
                    return true;
                default:
                    return false;
            }
        }

        private void confirmDelete(final ActionMode mode) {
            new AlertDialog.Builder(PerformanceListActivity.this)
                    .setMessage(getResources().getQuantityString(R.plurals.alert_remove_performances,
                            mListView.getCheckedItemCount(), mListView.getCheckedItemCount()))
                    .setPositiveButton(R.string.btn_remove, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSelectedItems(mode);
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .create().show();
        }

        private void deleteSelectedItems(ActionMode mode) {
            long[] selected = mListView.getCheckedItemIds();
            for (int i = 0; i < selected.length; i++) {
                PerformanceInfo removed = JsonLibrary.getInstance()
                        .removePerformanceWithItemId(PerformanceListActivity.this, selected[i]);
                mPerformances.remove(removed);
            }
            mode.finish(); // Action picked, so close the CAB
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
