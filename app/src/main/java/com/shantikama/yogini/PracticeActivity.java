package com.shantikama.yogini;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

public class PracticeActivity extends AppCompatActivity {
    private static final String TAG = "PracticeActivity";

    private static final String AUDIO_URL_START = "android.resource://com.shantikama.yogini/raw/";
    public static final int MILLIS_BETWEEN_ASANAS = 1000;

    private AsanaController mAsanaController;

    private MediaPlayer mAudioPlayer;
    private CountDownTimer mCountdownTimer;
    private boolean mIsWaitingForAsana;

    private FloatingActionButton mFab;

    private long mCurrentMillisRemaining;

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
        showPauseButton();

        setupAudioPlayer();

        final Reader asanasJson = new InputStreamReader(getResources().openRawResource(R.raw.asanas));
        mAsanaController = new AsanaController(GsonUtils.newGson().fromJson(asanasJson, Asanas.class));
    }

    private void setupAudioPlayer() {
        mAudioPlayer = newMediaPlayer(this);

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
                Log.i(TAG, "Media complete");
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
        mAsanaController.startPractice();
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
        }

        return super.onOptionsItemSelected(item);
    }

    void show(Asana asana) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PracticeActivityFragment fragment = (PracticeActivityFragment) fragmentManager
                .findFragmentById(R.id.fragment);
        fragment.updateAsana(asana);
    }

    void playAudio(String audio) {
        try {
            mAudioPlayer.setDataSource(this, Uri.parse(AUDIO_URL_START + audio));
            mAudioPlayer.prepare();
            resumeAudio();
        } catch (IOException e) {
            // TODO handle
            e.printStackTrace();
        }
    }

    void pauseAudio() {
        mAudioPlayer.pause();
    }

    private void resumeAudio() throws IOException {
        mAudioPlayer.start();
    }

    void waitForAsana(int numSecs) {
        // Callback occurs 10 times per second
        mCountdownTimer = new CountDownTimer(numSecs * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                mCurrentMillisRemaining = millisUntilFinished;
                ((PracticeActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment)).onTick10TimesPerSecond(mCurrentMillisRemaining);
            }

            @Override
            public void onFinish() {
                mIsWaitingForAsana = false;
                mCountdownTimer = null;
                mAsanaController.continuePractice();
            }
        }.start();
        mIsWaitingForAsana = true;
    }

    private void fabPressed() {
        try {
            if (mAudioPlayer.isPlaying()) {
                pauseAudio();
                showPlayButton();
            } else if (mIsWaitingForAsana) {
                if (mCountdownTimer == null) {
                    waitForAsana((int) mCurrentMillisRemaining / 1000);
                    showPauseButton();
                } else {
                    mCountdownTimer.cancel();
                    mCountdownTimer = null;
                    showPlayButton();
                }
            } else {
                resumeAudio();
                showPauseButton();
            }
        } catch (IOException e) {
            // TODO Show problem to user
            e.printStackTrace();
        }
    }

    private void showPauseButton() {
        mFab.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void showPlayButton() {
        mFab.setImageResource(android.R.drawable.ic_media_play);
    }

    private class AsanaController {
        private static final int STATE_NOT_YET_STARTED = 0;
        private static final int STATE_PLAYING_BEGIN_AUDIO = 1;
        private static final int STATE_PERFORMING_ASANA = 2;
        private static final int STATE_PLAYING_MULTI_PART_AUDIO = 3;
        private static final int STATE_WAITING_MULTI_PART = 4;
        private static final int STATE_PLAYING_END_AUDIO = 5;

        private Asanas mAsanas;

        private Iterator<Asana> mAsanaIterator;
        private Iterator<Asana.SequenceItem> mAsanaPartIterator;
        private Asana mCurAsana;
        private Asana.SequenceItem mCurAsanaPart;

        private int mCurState = STATE_NOT_YET_STARTED;

        public AsanaController(Asanas asanas) {
            mAsanas = asanas;
        }

        public void startPractice() {
            continuePractice();
        }

        public void continuePractice() {
            switch (mCurState) {
                case STATE_NOT_YET_STARTED:
                    mAsanaIterator = mAsanas.getAsanas().iterator();
                    startNextAsana();
                    break;
                case STATE_PLAYING_BEGIN_AUDIO:
                    mCurState = STATE_PERFORMING_ASANA;
                    waitForAsana(mCurAsana.time);
                    break;
                case STATE_PERFORMING_ASANA:
                    if (mCurAsana.isMultiPart()) {
                        mAsanaPartIterator = mCurAsana.multiPart.iterator();
                        mCurAsanaPart = mAsanaPartIterator.next();
                        mCurState = STATE_PLAYING_MULTI_PART_AUDIO;
                        playAudio(mCurAsanaPart.audio);
                    } else {
                        playEndAudio();
                    }
                    break;
                case STATE_PLAYING_MULTI_PART_AUDIO:
                    mCurState = STATE_WAITING_MULTI_PART;
                    waitForAsana(mCurAsanaPart.pause);
                    break;
                case STATE_WAITING_MULTI_PART:
                    if (mAsanaPartIterator.hasNext()) {
                        mCurAsanaPart = mAsanaPartIterator.next();
                        mCurState = STATE_PLAYING_MULTI_PART_AUDIO;
                        playAudio(mCurAsanaPart.audio);
                    } else {
                        playEndAudio();
                    }
                    break;
                case STATE_PLAYING_END_AUDIO:
                    if (mAsanaIterator.hasNext()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startNextAsana();
                            }
                        }, MILLIS_BETWEEN_ASANAS);
                    } else {
                        // finished
                    }
                    break;
            }
        }

        private void startNextAsana() {
            mCurAsana = mAsanaIterator.next();
            show(mCurAsana);
            playBeginAudio();
        }

        private void playBeginAudio() {
            mCurState = STATE_PLAYING_BEGIN_AUDIO;
            playAudio(mCurAsana.audioBegin);
        }

        private void playEndAudio() {
            mCurState = STATE_PLAYING_END_AUDIO;
            playAudio(mCurAsana.audioEnd);
        }
    }

    /**
     * Create a MediaPlayer which doesn't give the error
     * E/MediaPlayer: Should have subtitle controller already set
     *
     * @return a new MediaPlayer object
     * @link http://stackoverflow.com/a/20149754/355039
     */
    static MediaPlayer newMediaPlayer(Context context) {

        MediaPlayer mediaplayer = new MediaPlayer();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");

            Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

            Object subtitleInstance = constructor.newInstance(context, null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
            //Log.e("", "subtitle is setted :p");
        } catch (Exception e) {
        }

        return mediaplayer;
    }
}
