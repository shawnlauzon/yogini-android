package com.santikama.yogini;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private static final String TAG = "PracticeActivityFragment";

    private Asanas mAsanas;

    public PracticeActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsanas = loadJson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_practice, container, false);
        ((TextView) root.findViewById(R.id.text)).setText(mAsanas.toString());
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        performAsanas();
    }

    private void performAsanas() {
        for (Asana asana : mAsanas.getAsanas()) {
            performAsana(asana);
        }
    }

    private void performAsana(Asana asana) {
        ((TextView) getView().findViewById(R.id.text)).setText(asana.toString());
    }

    private Asanas loadJson() {
        final Reader asanasJson = new InputStreamReader(getResources().openRawResource(R.raw.asanas));
        return new Gson().fromJson(asanasJson, Asanas.class);
    }
}
