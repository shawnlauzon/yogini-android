package com.santikama.yogini;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * A placeholder fragment containing a simple view.
 */
public class PracticeActivityFragment extends Fragment {

    public PracticeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJson();
    }

    private void loadJson() {
        final Reader asanasJson = new InputStreamReader(getResources().openRawResource(R.raw.asanas));

        final Gson gson = new Gson();

        final Asanas asanas = gson.fromJson(asanasJson, Asanas.class);
        ((TextView) getView().findViewById(R.id.text)).setText(asanas.toString());
    }
}
