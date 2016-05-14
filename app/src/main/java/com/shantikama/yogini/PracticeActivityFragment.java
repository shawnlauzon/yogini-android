package com.shantikama.yogini;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class PracticeActivityFragment extends Fragment {
    private static final String TAG = "PracticeActivityFragment";

    private static final String TIMER_FORMAT = "%02d:%02d";

    private TextView mAsanaName;
    private TextView mTimer;
    private ProgressBar mProgress;

    public PracticeActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_practice, container, false);

        mAsanaName = (TextView) root.findViewById(R.id.name);
        mTimer = (TextView) root.findViewById(R.id.timer);
        mProgress = (ProgressBar) root.findViewById(R.id.progress);

        return root;
    }

    private void setTimerText(long millisUntilFinished) {
        mTimer.setText(String.format(Locale.getDefault(), TIMER_FORMAT,
                millisUntilFinished / 60000, millisUntilFinished / 1000 % 60));
    }

    void updateAsana(Asana asana) {
        mAsanaName.setText(asana.getName());

        int numSecs = asana.getTime();
        setTimerText(numSecs * 1000);

        mProgress.setProgress(0);
        mProgress.setMax(numSecs * 1000);
    }

    void onPracticeStarted() {
    }

    void onPracticeResumed() {
    }

    void onPracticePaused() {
    }

    void onTick10TimesPerSecond(long millisUntilFinished) {
        setTimerText(millisUntilFinished);

        // Because the tick is 10 times a second and our progress is in milliseconds, we increment
        // progress by 100.
        mProgress.incrementProgressBy(20);
    }
}