package com.shantikama.yogini.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.shantikama.yogini.Asana;
import com.shantikama.yogini.Asanas;
import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.R;

import java.util.List;

/**
 * An activity representing a list of Asanas. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AsanaDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AsanaListActivity extends AppCompatActivity {

    public static final String ARG_PRACTICE_ID = "practice_id";

    // FIXME share better
    Asanas mAsanas;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asana_list);

        mAsanas = JsonLibrary.getInstance().getAsanas(this,
                getIntent().getStringExtra(ARG_PRACTICE_ID));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getIntent().getStringExtra(mAsanas.name));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Add new asana
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View listView = findViewById(R.id.asana_list);
        assert listView != null;
        setupListView((ListView) listView);

        if (findViewById(R.id.asana_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListView(@NonNull ListView listView) {
        listView.setAdapter(new AsanaListViewAdapter(mAsanas.asanas));
        listView.setMultiChoiceModeListener(new AsanaMultiChoiceModalListener(listView));
        listView.setOnItemClickListener(new OnAsanaClickListener());
    }

    class AsanaListViewAdapter extends ArrayAdapter<Asana> {
        private final List<Asana> mAsanas;

        public AsanaListViewAdapter(List<Asana> items) {
            super(AsanaListActivity.this, R.layout.asana_list_content, items);
            mAsanas = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createView(parent);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mAsana = getItem(position);
            holder.mNameView.setText(holder.mAsana.name);

            // TODO Rather than text, make this an image, perhaps like a pie chart of 10 minutes
            if (holder.mAsana.time > 0) {
                final int timeMins = holder.mAsana.time / 60;
                holder.mTimeView.setText(getResources().getQuantityString(
                        R.plurals.asana_time, timeMins, timeMins, timeMins));
            } else {
                holder.mTimeView.setText("");
            }
            return convertView;
        }

        private View createView(ViewGroup parent) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.asana_list_content, parent, false);
            view.setTag(new ViewHolder(view));
            return view;
        }

        @Override
        public int getCount() {
            return mAsanas.size();
        }
    }

    static class ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mTimeView;
        public Asana mAsana;

        public ViewHolder(View view) {
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mTimeView = (TextView) view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    class OnAsanaClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(AsanaDetailFragment.ARG_PRACTICE_ID,
                        getIntent().getStringExtra(ARG_PRACTICE_ID));
                arguments.putString(AsanaDetailFragment.ARG_ASANA_ID, holder.mAsana.id);
                AsanaDetailFragment fragment = new AsanaDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.asana_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, AsanaDetailActivity.class);
                intent.putExtra(AsanaDetailFragment.ARG_PRACTICE_ID,
                        getIntent().getStringExtra(ARG_PRACTICE_ID));
                intent.putExtra(AsanaDetailFragment.ARG_ASANA_ID, holder.mAsana.id);
                context.startActivity(intent);
            }

        }
    }

    class AsanaMultiChoiceModalListener implements AbsListView.MultiChoiceModeListener {

        private ListView mListView;

        AsanaMultiChoiceModalListener(ListView listView) {
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
            inflater.inflate(R.menu.asana_list_context, menu);
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
                case R.id.action_time:
                    displayTimeChanger(mode);
                    return true;
                default:
                    return false;
            }
        }

        private void confirmDelete(final ActionMode mode) {
            new AlertDialog.Builder(AsanaListActivity.this)
                    .setMessage(getResources().getQuantityString(R.plurals.alert_remove_asanas,
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
            SparseBooleanArray selected = mListView.getCheckedItemPositions();
            System.out.println("Should delete " + selected);
            mode.finish(); // Action picked, so close the CAB
        }

        private void displayTimeChanger(final ActionMode mode) {
            NumberPicker time = new NumberPicker(AsanaListActivity.this);
            time.setMinValue(0);
            time.setMaxValue(60);

            if (mListView.getCheckedItemCount() == 1) {
                time.setValue(mAsanas.asanas.get(mListView.getCheckedItemPositions().keyAt(0)).time / 60);
            }

            AlertDialog dialog = new AlertDialog.Builder(AsanaListActivity.this)
                    .setTitle(R.string.dialog_title_minutes)
                    .setView(time)
                    .setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSelectedItems(mode);
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .create();
            dialog.show();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
