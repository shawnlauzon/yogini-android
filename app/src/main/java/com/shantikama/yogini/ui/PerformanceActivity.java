package com.shantikama.yogini.ui;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.common.collect.ImmutableList;
import com.shantikama.yogini.Asana;
import com.shantikama.yogini.JsonLibrary;
import com.shantikama.yogini.Performance;
import com.shantikama.yogini.R;
import com.shantikama.yogini.utils.MediaPlayerUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

public class PerformanceActivity extends AppCompatActivity {
    private static final String TAG = "PerformanceActivity";

    public static final String ARG_PRACTICE_ID = "practice_id";

    private static final String AUDIO_URL_START = "android.resource://com.shantikama.yogini/raw/";

    private static final String TAG_IS_STARTED = "TAG_IS_STARTED";
    private static final String TAG_PERFORMANCE_MILIS_REMAINING = "TAG_PERFORMANCE_MILIS_REMAINING";
    private static final String TAG_IS_COUNTDOWN_PAUSED = "TAG_IS_COUNTDOWN_PAUSED";
    private static final String TAG_IS_SHOWING_TIMER = "TAG_IS_SHOWING_TIMER";
    private static final String TAG_IS_PRACTICE_PAUSED = "TAG_IS_PRACTICE_PAUSED";
    private static final String TAG_AUDIO_PLAYER_POSITION = "TAG_AUDIO_PLAYER_POSITION";

    private AsanaController mAsanaController;
    private boolean mIsStarted = false;

    private MediaPlayer mAudioPlayer;

    private CountDownTimer mPerformanceTimer;
    private boolean mIsCountdownPaused;
    private int mPerformanceMillisRemaining;
    private boolean mIsShowingTimer;

    private FloatingActionButton mFab;
    private boolean mIsPracticePaused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPressed();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupAudioPlayer();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        Performance asanas = JsonLibrary.getInstance().getPerformance(this, bundle.getString(ARG_PRACTICE_ID));
        setTitle(asanas.getName());
        mAsanaController = new AsanaController(asanas);

        if (savedInstanceState != null) {
            initState(savedInstanceState);
        }

        updateFabImage();

        // TODO Show a nice image of the entire practice
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupAudioPlayer() {
        mAudioPlayer = MediaPlayerUtils.newMediaPlayer(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAudioPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());
        }

        mAudioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "MediaPlayer error: " + what + "(" + extra + ")");
                Thread.dumpStack();
                // TODO display to user
                return false;
            }
        });

        mAudioPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "MediaPlayer info: " + what + "(" + extra + ")");
                return false;
            }
        });

        mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAudioPlayer.reset();
                mAsanaController.continuePractice();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.reset();
        mAudioPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(TAG_IS_STARTED, mIsStarted);
        if (mIsStarted) {
            outState.putBoolean(TAG_IS_COUNTDOWN_PAUSED, mIsCountdownPaused);
            outState.putBoolean(TAG_IS_PRACTICE_PAUSED, mIsPracticePaused);
            outState.putBoolean(TAG_IS_SHOWING_TIMER, mIsShowingTimer);
            outState.putInt(TAG_PERFORMANCE_MILIS_REMAINING, mPerformanceMillisRemaining);
            if (mAudioPlayer.isPlaying()) {
                outState.putInt(TAG_AUDIO_PLAYER_POSITION, mAudioPlayer.getCurrentPosition());
            }
            mAsanaController.onSaveInstanceState(outState);

            if (mPerformanceTimer != null) {
                mPerformanceTimer.cancel();
                mPerformanceTimer = null;
            }
        }
    }

    private void initState(Bundle savedInstanceState) {
        mIsStarted = savedInstanceState.getBoolean(TAG_IS_STARTED);
        if (mIsStarted) {
            mIsCountdownPaused = savedInstanceState.getBoolean(TAG_IS_COUNTDOWN_PAUSED);
            mIsPracticePaused = savedInstanceState.getBoolean(TAG_IS_PRACTICE_PAUSED);
            mIsShowingTimer = savedInstanceState.getBoolean(TAG_IS_SHOWING_TIMER);
            mPerformanceMillisRemaining = savedInstanceState.getInt(TAG_PERFORMANCE_MILIS_REMAINING);

            mAsanaController.initState(savedInstanceState);

            final int audioPlayerPosition = savedInstanceState.getInt(TAG_AUDIO_PLAYER_POSITION, -1);
            if (audioPlayerPosition >= 0) {
                mAudioPlayer.seekTo(savedInstanceState.getInt(TAG_AUDIO_PLAYER_POSITION));
            }
            if (mIsPracticePaused) {
                mAudioPlayer.pause();
            }
        }
    }

    void show(Asana asana, int requestTimeMillis) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PerformanceActivityFragment fragment = (PerformanceActivityFragment) fragmentManager
                .findFragmentById(R.id.fragment);
        fragment.updateAsana(asana, getActualTime(requestTimeMillis));
    }

    private int getActualTime(int requestedTimeMillis) {
        return mPerformanceMillisRemaining > 0 ? mPerformanceMillisRemaining : requestedTimeMillis;
    }

    void playAudio(String audio) {
        if (audio == null) {
            Log.d(TAG, "Skipping null audio");
            mAsanaController.continuePractice();
        } else {
            Log.d(TAG, String.format("Playing audio %s ...", audio));
            try {
                mAudioPlayer.setDataSource(this, Uri.parse(AUDIO_URL_START + audio));
                mAudioPlayer.prepare();
                resumeAudio();
            } catch (IOException e) {
                // TODO handle
                e.printStackTrace();
            }
        }
    }

    void pauseAudio() {
        mAudioPlayer.pause();
    }

    private void resumeAudio() throws IOException {
        mAudioPlayer.start();
    }

    void waitFor(final int numMillisInPhase, final boolean showTimer) {
        final long waitTimeMs = getActualTime(numMillisInPhase);
        mIsShowingTimer = showTimer;

        mPerformanceTimer = new CountDownTimer(waitTimeMs, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mPerformanceMillisRemaining = (int) millisUntilFinished;
                if (mIsShowingTimer) {
                    ((PerformanceActivityFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment)).updateTimeRemaining(mPerformanceMillisRemaining);
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, String.format("Finished timer %s", mPerformanceTimer));
                mPerformanceTimer = null;
                mPerformanceMillisRemaining = -1;
                mAsanaController.continuePractice();
            }
        };
        Log.d(TAG, String.format("Created timer %s for %d ms", mPerformanceTimer, waitTimeMs));

        mPerformanceTimer.start();
    }

    private void fabPressed() {
        try {
            if (mIsPracticePaused) { // user pressed the play button
                mIsPracticePaused = false;
                if (!mIsStarted) { // Only at very beginning of routine
                    mAsanaController.startPractice();
                    mIsStarted = true;
                } else if (mIsCountdownPaused) {
                    waitFor(mPerformanceMillisRemaining, mIsShowingTimer);
                    mIsCountdownPaused = false;
                } else {
                    resumeAudio();
                }
            } else { // user pressed the pause button
                mIsPracticePaused = true;
                if (mAudioPlayer.isPlaying()) {
                    pauseAudio();
                } else {
                    mPerformanceTimer.cancel();
                    mPerformanceTimer = null;
                    mIsCountdownPaused = true;
                }
            }
        } catch (IOException e) {
            // TODO Show problem to user
            e.printStackTrace();
        }
        updateFabImage();
    }

    private void updateFabImage() {
        final int drawableId;
        if (mIsPracticePaused) {
            drawableId = android.R.drawable.ic_media_play;
        } else {
            drawableId = android.R.drawable.ic_media_pause;
        }
        mFab.setImageResource(drawableId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_details) {
            Intent intent = new Intent(this, AsanaListActivity.class);
            intent.putExtra(AsanaListActivity.ARG_PRACTICE_ID,
                    getIntent().getStringExtra(ARG_PRACTICE_ID));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_skip) {
            if (mPerformanceTimer != null) {
                mPerformanceTimer.cancel();
                mPerformanceTimer.onTick(0); // Reset the display
                mPerformanceTimer = null;
            } else if (mAudioPlayer.isPlaying()) {
                mAudioPlayer.reset();
            }
            mAsanaController.continueNextPhase();
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsanaController {
        private static final String TAG_CUR_ASANA_ID = "TAG_CUR_ASANA_ID";
        private static final String TAG_CUR_ASANA_SEQUENCE_ITEM_POS = "TAG_CUR_ASANA_SEQUENCE_ITEM_POS";
        private static final String TAG_CUR_PHASE = "TAG_CUR_PHASE";
        private static final String TAG_CUR_STATE = "TAG_CUR_STATE";

        // Phase numbers must be in ascending order with no gaps
        private static final int PHASE_IDLE = 0;
        private static final int PHASE_ANNOUNCE = 1;
        private static final int PHASE_TECHNIQUE = 2;
        private static final int PHASE_CONCENTRATION = 3;
        private static final int PHASE_BEGIN = 4;
        private static final int PHASE_PERFORM = 5;
        private static final int PHASE_END = 6;
        private static final int PHASE_AWARENESS = 7;
        private static final int PHASE_STRETCH = 8;
        private final ImmutableList<String> PHASE_STRS = ImmutableList.of("IDLE", "ANNOUNCE",
                "TECHNIQUE", "CONCENTRATION", "BEGIN", "PERFORM", "END", "AWARENESS", "STRETCH");

        private static final int STATE_IDLE = 0;
        private static final int STATE_PLAYING = 1;
        private static final int STATE_WAITING = 2;
        private final ImmutableList<String> STATE_STRS = ImmutableList.of("IDLE", "PLAYING",
                "WAITING");

        private Performance mPerformance;

        private Iterator<Asana> mAsanaIterator;
        private ListIterator<Asana.SequenceItem> mAsanaSequenceIterator;

        private Asana mCurAsana;
        private Asana.SequenceItem mCurAsanaSequenceItem;

        private int mCurPhase = PHASE_IDLE;
        private int mCurState = STATE_IDLE;

        public AsanaController(Performance performance) {
            mPerformance = performance;
        }

        void onSaveInstanceState(Bundle outState) {
            if (mCurAsana != null) {
                outState.putString(TAG_CUR_ASANA_ID, mCurAsana.getId());
            }
            if (mAsanaSequenceIterator != null) {
                outState.putInt(TAG_CUR_ASANA_SEQUENCE_ITEM_POS, mAsanaSequenceIterator.previousIndex());
            }

            outState.putInt(TAG_CUR_PHASE, mCurPhase);
            outState.putInt(TAG_CUR_STATE, mCurState);
        }

        void initState(Bundle savedInstanceState) {
            final String savedAsanaId = savedInstanceState.getString(TAG_CUR_ASANA_ID);
            if (savedAsanaId != null) {
                mAsanaIterator = mPerformance.getAsanas().iterator();
                while (mAsanaIterator.hasNext()) {
                    // Advance to the asana we saved
                    mCurAsana = mAsanaIterator.next();
                    if (mCurAsana.getId().equals(savedAsanaId)) {
                        break;
                    }
                }
                final int savedSequenceItemPos = savedInstanceState.getInt(TAG_CUR_ASANA_SEQUENCE_ITEM_POS, -1);
                if (savedSequenceItemPos >= 0) {
                    Log.d(TAG, "Advancing to sequence " + savedSequenceItemPos);
                    assert mCurAsana.getSequence() != null;
                    mAsanaSequenceIterator = mCurAsana.getSequence().listIterator();
                    int pos = 0;
                    do {
                        mCurAsanaSequenceItem = mAsanaSequenceIterator.next();
                    } while (++pos <= savedSequenceItemPos);
                }
            }

            mCurPhase = savedInstanceState.getInt(TAG_CUR_PHASE);
            mCurState = savedInstanceState.getInt(TAG_CUR_STATE);

            show(mCurAsana, mCurAsana.getTime());
            handleState();
        }

        public void startPractice() {
            if (mAsanaIterator == null) {
                // On rotation, this will be already advanced to the correct location
                mAsanaIterator = mPerformance.getAsanas().iterator();
            }

            continuePractice();
        }

        public void continuePractice() {
            advanceState();
            handleState();
        }

        public void continueNextPhase() {
            Log.d(TAG, String.format("Finishing early phase %s", PHASE_STRS.get(mCurPhase)));
            advancePhase();
            handleState();
        }

        private void advanceState() {
            if (mCurState == STATE_PLAYING) {
                mCurState = STATE_WAITING;
            } else { // just finished WAITING state
                advancePhase();
            }
        }

        private void advancePhase() {
            mCurState = STATE_PLAYING;

            if (mCurPhase == PHASE_AWARENESS && mAsanaSequenceIterator.hasNext()) {
                mCurPhase = PHASE_TECHNIQUE;
                mCurAsanaSequenceItem = mAsanaSequenceIterator.next();

            } else if (mCurPhase == PHASE_IDLE || mCurPhase == PHASE_STRETCH) {
                advanceAsana();
            } else {
                ++mCurPhase;
            }

            if (isPhaseSkipped()) {
                Log.d(TAG, String.format("Skipping phase %s", PHASE_STRS.get(mCurPhase)));
                advancePhase();
            }

            if (mCurPhase != PHASE_PERFORM) { // there is no perform.playing state)
                mCurState = STATE_PLAYING;
            }
        }

        private void handleState() {
            Log.d(TAG, String.format("handleState(%s, %s)", PHASE_STRS.get(mCurPhase),
                    STATE_STRS.get(mCurState)));

            if (mCurPhase == PHASE_ANNOUNCE) {
                show(mCurAsana, mCurAsana.getTime() * 1000);
            } else if (mCurPhase == PHASE_TECHNIQUE) {
                if (mCurAsanaSequenceItem.getTime() > 0) {
                    show(mCurAsana, mCurAsanaSequenceItem.getTime() * 1000);
                }
            }

            if (mCurState == STATE_WAITING) {
                waitFor(getPhasePauseMillis(), mCurPhase == PHASE_PERFORM);
            } else if (mCurState == STATE_PLAYING) {
                playAudio(getPhaseAudio());
            }
        }

        private void advanceAsana() {
            if (mAsanaIterator.hasNext()) {
                mCurAsana = mAsanaIterator.next();
                assert mCurAsana.getSequence() != null;
                mAsanaSequenceIterator = mCurAsana.getSequence().listIterator();
                mCurAsanaSequenceItem = mAsanaSequenceIterator.next();
                mCurPhase = PHASE_ANNOUNCE;
                mCurState = STATE_PLAYING;
            } else {
                finish();
                // FIXME Should be a better way to quit out of this.
                throw new RuntimeException();
            }
        }

        private String getPhaseAudio() {
            final String phaseAudio;
            switch (mCurPhase) {
                case PHASE_ANNOUNCE:
                    phaseAudio = mCurAsana.getAnnounceAudio();
                    break;
                case PHASE_TECHNIQUE:
                    phaseAudio = mCurAsanaSequenceItem.getTechniqueAudio();
                    break;
                case PHASE_CONCENTRATION:
                    phaseAudio = mCurAsanaSequenceItem.getConcentrationAudio();
                    break;
                case PHASE_BEGIN:
                    phaseAudio = mPerformance.getBeginAudio();
                    break;
                case PHASE_END:
                    phaseAudio = mPerformance.getEndAudio();
                    break;
                case PHASE_AWARENESS:
                    phaseAudio = mCurAsanaSequenceItem.getAwarenessAudio();
                    break;
                case PHASE_STRETCH:
                    phaseAudio = mCurAsana.getStretchAudio();
                    break;
                default:
                    phaseAudio = null;
            }
            return phaseAudio;
        }

        private int getPhasePauseMillis() {
            final int phasePauseSecs;
            switch (mCurPhase) {
                case PHASE_ANNOUNCE:
                    phasePauseSecs = mCurAsana.getAnnouncePause();
                    break;
                case PHASE_TECHNIQUE:
                    phasePauseSecs = mCurAsanaSequenceItem.getTechniquePause();
                    break;
                case PHASE_CONCENTRATION:
                    phasePauseSecs = mCurAsanaSequenceItem.getConcentrationPause();
                    break;
                case PHASE_BEGIN:
                    phasePauseSecs = mCurAsanaSequenceItem.getBeginPause();
                    break;
                case PHASE_PERFORM:
                    final int time = mCurAsanaSequenceItem.getTime();
                    phasePauseSecs = time > 0 ? time : mCurAsana.getTime();
                    break;
                case PHASE_END:
                    phasePauseSecs = mCurAsanaSequenceItem.getEndPause();
                    break;
                case PHASE_AWARENESS:
                    phasePauseSecs = mCurAsanaSequenceItem.getAwarenessPause();
                    break;
                case PHASE_STRETCH:
                    phasePauseSecs = mCurAsana.getStretchPause();
                    break;
                default:
                    phasePauseSecs = 0;
            }
            return phasePauseSecs * 1000;
        }

        private boolean isPhaseSkipped() {
            return ((mCurPhase == PHASE_TECHNIQUE && mCurAsanaSequenceItem.isSkipped(Asana.SequenceItem.PHASE_TECHNIQUE)) ||
                    (mCurPhase == PHASE_CONCENTRATION && mCurAsanaSequenceItem.isSkipped(Asana.SequenceItem.PHASE_CONCENTRATION)) ||
                    (mCurPhase == PHASE_BEGIN && mCurAsanaSequenceItem.isSkipped(Asana.SequenceItem.PHASE_BEGIN)) ||
                    (mCurPhase == PHASE_END && mCurAsanaSequenceItem.isSkipped(Asana.SequenceItem.PHASE_END)) ||
                    (mCurPhase == PHASE_AWARENESS && mCurAsanaSequenceItem.isSkipped(Asana.SequenceItem.PHASE_AWARENESS)));
        }
    }
}
