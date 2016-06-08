package com.shantikama.yogini.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.shantikama.yogini.Asana;
import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.Performance;
import com.shantikama.yogini.R;

import java.util.List;

public class PerformanceBuilderActivity extends NavigationDrawerActivity {

    private Performance mPerformance;
    private ArrayAdapter<Asana> mAsanasAdapter;

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

        View listView = findViewById(android.R.id.list);
        assert listView != null;
        setupListView((ListView) listView);
    }

    private void setupListView(ListView listView) {
        mAsanasAdapter = new AsanaListViewAdapter(mPerformance.getAsanas());
        listView.setAdapter(mAsanasAdapter);
    }

    class AsanaListViewAdapter extends ArrayAdapter<Asana> {
        private final List<Asana> mAsanas;

        public AsanaListViewAdapter(List<Asana> items) {
            super(PerformanceBuilderActivity.this, R.layout.performance_builder_list_content, items);
            mAsanas = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createView(parent);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mAsana = getItem(position);
            holder.mImageView.setImageResource(R.drawable.agama_yoga_logo);

            return convertView;
        }

        private View createView(ViewGroup parent) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.performance_builder_list_content, parent, false);
            view.setTag(new ViewHolder(view));
            return view;
        }

        @Override
        public int getCount() {
            return mAsanas.size();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return mAsanas.get(position).getItemId();
        }
    }

    static class ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public Asana mAsana;

        public ViewHolder(View view) {
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}

