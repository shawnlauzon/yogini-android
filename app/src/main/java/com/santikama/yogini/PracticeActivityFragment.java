package com.santikama.yogini;

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

    private static final String ARG_ASANA_NAME = "asana_name";
    private static final String ARG_ASANA_TIME = "asana_time";

    private static final String TIMER_FORMAT = "%02d:%02d";

    private TextView mAsanaName;
    private TextView mTimer;
    private ProgressBar mProgress;

    static PracticeActivityFragment newInstance(Asana asana) {
        PracticeActivityFragment f = new PracticeActivityFragment();
        Bundle args = new Bundle();

        args.putString(ARG_ASANA_NAME, asana.getName());
        args.putInt(ARG_ASANA_TIME, asana.getTime());

        f.setArguments(args);
        return f;
    }

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

        mAsanaName.setText(getArguments().getString(ARG_ASANA_NAME));

        int numSecs = getArguments().getInt(ARG_ASANA_TIME);
        setTimerText(numSecs * 1000);

        mProgress.setMax(numSecs * 1000);

        return root;
    }

    private void setTimerText(long millisUntilFinished) {
        mTimer.setText(String.format(Locale.getDefault(), TIMER_FORMAT,
                millisUntilFinished / 60000, millisUntilFinished / 1000 % 60));
    }

    void onPracticeStarted() {
    }

    void onPracticeResumed() {
    }

    void onPracticePaused() {
    }

    void onTick(long millisUntilFinished) {
        setTimerText(millisUntilFinished);
        mProgress.incrementProgressBy(100);
    }
}
