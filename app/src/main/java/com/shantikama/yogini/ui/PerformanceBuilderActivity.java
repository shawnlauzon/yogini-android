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
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mAsanasAdapter = new AsanaAdapter(mPerformance.getAsanas());
        recyclerView.setAdapter(mAsanasAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupSpinner(Spinner spinner) {
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.phases)));
    }

    private class AsanaAdapter extends RecyclerView.Adapter<AsanaViewHolder> {
        private final List<Asana> mAsanas;
        private Asana mSelectedAsana;
        private View mSelectedView;

        public AsanaAdapter(List<Asana> items) {
            mAsanas = items;
            mSelectedAsana = mAsanas.get(0);
            setTitle(mSelectedAsana.getName());
            setHasStableIds(true);
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
                    onViewClicked(v);
                }
            });
            return new AsanaViewHolder(newView);
        }

        private void onViewClicked(View v) {
            if (v.getBackground() != null) { // deselect the view
                v.setBackgroundResource(0);
                mSelectedAsana = null;
                mSelectedView = null;
            } else { // select the view
                if (mSelectedView != null) {
                    // deselect the previously selected view
                    mSelectedView.setBackgroundResource(0);
                }
                mSelectedView = v;
                mSelectedView.setBackgroundResource(R.drawable.selection_background);
                mSelectedAsana = (Asana) v.getTag();
                setTitle(mSelectedAsana.getName());
            }
        }

        @Override
        public void onBindViewHolder(AsanaViewHolder holder, int position) {
            View selectedView = holder.itemView;

            // TODO Display an image from the asana
            holder.mImageView.setImageResource(R.drawable.agama_yoga_logo);

            Asana selectedAsana = mAsanas.get(position);
            if (mSelectedAsana == selectedAsana) {
                mSelectedView = selectedView;
                mSelectedView.setBackgroundResource(R.drawable.selection_background);
            } else {
                selectedView.setBackgroundResource(0);
            }

            selectedView.setTag(selectedAsana);
        }

        @Override
        public long getItemId(int position) {
            return mAsanas.get(position).getItemId();
        }
    }

    static class AsanaViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public AsanaViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}

