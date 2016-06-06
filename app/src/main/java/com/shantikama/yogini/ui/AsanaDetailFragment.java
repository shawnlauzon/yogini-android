package com.shantikama.yogini.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shantikama.yogini.Asana;
import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.R;

/**
 * A fragment representing a single Asana detail screen.
 * This fragment is either contained in a {@link AsanaListActivity}
 * in two-pane mode (on tablets) or a {@link AsanaDetailActivity}
 * on handsets.
 */
public class AsanaDetailFragment extends Fragment {

    public static final String ARG_PRACTICE_ID = "practice_id";
    public static final String ARG_ASANA_ID = "asana_id";

    private Asana mAsana;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AsanaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ASANA_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mAsana = JsonLibrary.getInstance().getAsanas(getActivity(),
                    getArguments().getString(ARG_PRACTICE_ID)).getById(getArguments().getString(ARG_ASANA_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mAsana.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.asana_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mAsana != null) {
            ((TextView) rootView.findViewById(R.id.asana_detail)).setText(String.valueOf(mAsana.time));
        }

        return rootView;
    }
}
