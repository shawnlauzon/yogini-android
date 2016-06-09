package com.shantikama.yogini.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.shantikama.yogini.Asana;
import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.Performance;
import com.shantikama.yogini.R;

import java.util.List;

public class PerformanceBuilderActivity extends NavigationDrawerActivity {

    private static final String TAG_SELECTED_POS = "selected_pos";

    private Performance mPerformance;
    private AsanaAdapter mAsanasAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_builder);

        mPerformance = JsonLibrary.getInstance().getPerformance(this, JsonLibrary.PERFORMANCE_ID_ALL_ASANAS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        setupDrawer(toolbar);

        View recyclerView = findViewById(android.R.id.list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        View spinner = findViewById(R.id.phase_spinner);
        assert spinner != null;
        setupSpinner((Spinner) spinner);

        if (savedInstanceState != null) {
            mAsanasAdapter.setSelectedPosition(savedInstanceState.getInt(TAG_SELECTED_POS));
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAsanasAdapter = new AsanaAdapter(recyclerView, mPerformance.getAsanas());
        recyclerView.setAdapter(mAsanasAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupSpinner(Spinner spinner) {
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.phases)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_SELECTED_POS, mAsanasAdapter.getSelectedPosition());
    }

    private class AsanaAdapter extends RecyclerView.Adapter<AsanaViewHolder> {
        private RecyclerView mRecyclerView;

        private final List<Asana> mAsanas;
        private int mSelectedPosition;
        private View mSelectedView;

        public AsanaAdapter(RecyclerView recyclerView, List<Asana> items) {
            mRecyclerView = recyclerView;
            mAsanas = items;
            setHasStableIds(true);
            setSelectedPosition(0);
        }

        int getSelectedPosition() {
            return mSelectedPosition;
        }

        void setSelectedPosition(int position) {
            mSelectedPosition = position;
            setTitle(mAsanas.get(mSelectedPosition).getName());
        }

        @Override
        public int getItemCount() {
            return mAsanas.size();
        }

        @Override
        public AsanaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = getLayoutInflater().inflate(
                    R.layout.performance_builder_list_content, parent, false);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!v.isSelected()) {
                        // We are selecting the view clicked
                        if (mSelectedView != null) {
                            // deselect the previously selected view
                            mSelectedView.setSelected(false);
                        }
                        mSelectedPosition = mRecyclerView.getChildAdapterPosition(v);
                        mSelectedView = v;
                        setTitle(mAsanas.get(mSelectedPosition).getName());
                    } else {
                        // We are deselecting the view clicked
                        setTitle("");
                        mSelectedPosition = -1;
                        mSelectedView = null;
                    }

                    // toggle the item clicked
                    v.setSelected(!v.isSelected());
                }
            });
            return new AsanaViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(AsanaViewHolder holder, int position) {
            // TODO Display an image from the asana
            holder.imageView.setImageResource(R.drawable.agama_yoga_logo);

            if (position == mSelectedPosition) {
                holder.itemView.setSelected(true);

                // keep track of the currently selected view when recycled
                mSelectedView = holder.itemView;
            } else {
                holder.itemView.setSelected(false);
            }
        }

        @Override
        public long getItemId(int position) {
            return mAsanas.get(position).getItemId();
        }
    }

    static class AsanaViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public AsanaViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}

