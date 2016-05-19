package com.shantikama.yogini;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

public class PracticeActivity extends AppCompatActivity {
    private static final String TAG = "PracticeActivity";

    private static final String AUDIO_URL_START = "android.resource://com.shantikama.yogini/raw/";

    private AsanaController mAsanaController;
    private boolean mIsStarted = false;

    private MediaPlayer mAudioPlayer;

    private CountDownTimer mPerformanceTimer;
    private boolean mIsCountdownPaused;
    private long mPerformanceMillisRemaining;
    private boolean isShowingTimer;

    private FloatingActionButton mFab;
    private boolean mIsPracticePaused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPressed();
            }
        });
        showPlayButton();

        setupAudioPlayer();

        final Reader asanasJson = new InputStreamReader(getResources().openRawResource(R.raw.asanas));
        mAsanaController = new AsanaController(GsonUtils.newGson().fromJson(asanasJson, Asanas.class));
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

    void show(Asana asana, int time) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PracticeActivityFragment fragment = (PracticeActivityFragment) fragmentManager
                .findFragmentById(R.id.fragment);
        fragment.updateAsana(asana, time);
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

    void waitFor(final int numSecs, final boolean showTimer) {
        Log.d(TAG, String.format("Waiting for %d seconds ...", numSecs));
        isShowingTimer = showTimer;

        mPerformanceTimer = new CountDownTimer(numSecs * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mPerformanceMillisRemaining = millisUntilFinished;
                if (isShowingTimer) {
                    ((PracticeActivityFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment)).updateTimeRemaining(mPerformanceMillisRemaining);
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, String.format("Finished timer %s", mPerformanceTimer));
                mPerformanceTimer = null;
                mAsanaController.continuePractice();
            }
        };
        Log.d(TAG, String.format("Created timer %s for %d seconds", mPerformanceTimer, numSecs));

        mPerformanceTimer.start();
    }

    private void fabPressed() {
        try {
            if (mIsPracticePaused) { // user pressed the play button
                showPauseButton();
                if (!mIsStarted) { // Only at very beginning of routine
                    mAsanaController.startPractice();
                    mIsStarted = true;
                } else if (mIsCountdownPaused) {
                    waitFor((int) mPerformanceMillisRemaining / 1000, isShowingTimer);
                    mIsCountdownPaused = false;
                } else {
                    resumeAudio();
                }
            } else { // user pressed the pause button
                showPlayButton();
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
    }

    private void showPauseButton() {
        mFab.setImageResource(android.R.drawable.ic_media_pause);
        mIsPracticePaused = false;
    }

    private void showPlayButton() {
        mFab.setImageResource(android.R.drawable.ic_media_play);
        mIsPracticePaused = true;
    }

    private class AsanaController {
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

        private Asanas mAsanas;

        private Iterator<Asana> mAsanaIterator;
        private Iterator<Asana.SequenceItem> mAsanaSequenceIterator;

        private Asana mCurAsana;
        private Asana.SequenceItem mCurAsanaSequenceItem;

        private int mCurPhase = PHASE_IDLE;
        private int mCurState = STATE_IDLE;

        public AsanaController(Asanas asanas) {
            mAsanas = asanas;
        }

        public void startPractice() {
            mAsanaIterator = mAsanas.asanas.iterator();
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
                show(mCurAsana, mCurAsana.time);
            } else if (mCurPhase == PHASE_TECHNIQUE) {
                if (mCurAsanaSequenceItem.time > 0) {
                    show(mCurAsana, mCurAsanaSequenceItem.time);
                }
            }

            if (mCurState == STATE_WAITING) {
                waitFor(getPhasePause(), mCurPhase == PHASE_PERFORM);
            } else if (mCurState == STATE_PLAYING) {
                playAudio(getPhaseAudio());
            }
        }

        private void advanceAsana() {
            if (mAsanaIterator.hasNext()) {
                mCurAsana = mAsanaIterator.next();
                mAsanaSequenceIterator = mCurAsana.sequence.iterator();
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
                    phaseAudio = mCurAsana.announceAudio;
                    break;
                case PHASE_TECHNIQUE:
                    phaseAudio = mCurAsanaSequenceItem.techniqueAudio;
                    break;
                case PHASE_CONCENTRATION:
                    phaseAudio = mCurAsanaSequenceItem.concentrationAudio;
                    break;
                case PHASE_BEGIN:
                    phaseAudio = mAsanas.beginAudio;
                    break;
                case PHASE_END:
                    phaseAudio = mAsanas.endAudio;
                    break;
                case PHASE_AWARENESS:
                    phaseAudio = mCurAsanaSequenceItem.awarenessAudio;
                    break;
                case PHASE_STRETCH:
                    phaseAudio = mCurAsana.stretchAudio;
                    break;
                default:
                    phaseAudio = null;
            }
            return phaseAudio;
        }

        private int getPhasePause() {
            final int phasePause;
            switch (mCurPhase) {
                case PHASE_ANNOUNCE:
                    phasePause = mCurAsana.announcePause;
                    break;
                case PHASE_TECHNIQUE:
                    phasePause = mCurAsanaSequenceItem.techniquePause;
                    break;
                case PHASE_CONCENTRATION:
                    phasePause = mCurAsanaSequenceItem.concentrationPause;
                    break;
                case PHASE_BEGIN:
                    phasePause = mCurAsanaSequenceItem.beginPause;
                    break;
                case PHASE_PERFORM:
                    phasePause = mCurAsanaSequenceItem.time > 0 ? mCurAsanaSequenceItem.time : mCurAsana.time;
                    break;
                case PHASE_END:
                    phasePause = mCurAsanaSequenceItem.endPause;
                    break;
                case PHASE_AWARENESS:
                    phasePause = mCurAsanaSequenceItem.awarenessPause;
                    break;
                case PHASE_STRETCH:
                    phasePause = mCurAsana.stretchPause;
                    break;
                default:
                    phasePause = 0;
            }
            return phasePause;
        }

        private boolean isPhaseSkipped() {
            return mCurAsanaSequenceItem.skip != null &&
                    ((mCurPhase == PHASE_TECHNIQUE && mCurAsanaSequenceItem.skip.technique) ||
                            (mCurPhase == PHASE_CONCENTRATION && mCurAsanaSequenceItem.skip.concentration) ||
                            (mCurPhase == PHASE_BEGIN && mCurAsanaSequenceItem.skip.begin) ||
                            (mCurPhase == PHASE_END && mCurAsanaSequenceItem.skip.end) ||
                            (mCurPhase == PHASE_AWARENESS && mCurAsanaSequenceItem.skip.awareness));
        }
    }

}
