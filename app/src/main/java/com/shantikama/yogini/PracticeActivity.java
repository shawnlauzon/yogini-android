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

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PracticeActivity extends AppCompatActivity {
    private static final String TAG = "PracticeActivity";

    private static final short PRACTICE_STATE_IDLE = 0;
    private static final short PRACTICE_STATE_PLAYING = 1;
    private static final short PRACTICE_STATE_PAUSED = 2;

    private static final String AUDIO_URL_START = "android.resource://com.shantikama.yogini/raw/";

    private Asanas mAsanas;
    private Asana mCurrentAsana;
    private int mCurrentAsanaPosition = -1;

    private short mPracticeState = PRACTICE_STATE_IDLE;
    private boolean mFinishedAsana = false;
    private MediaPlayer mAudioPlayer;
    private CountDownTimer mCountdownTimer;

    private FloatingActionButton mFab;

    private long mCurrentMillisRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Reader asanasJson = new InputStreamReader(getResources().openRawResource(R.raw.asanas));
        mAsanas = new Gson().fromJson(asanasJson, Asanas.class);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPressed();
            }
        });

        setupAudioPlayer();
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

                if (mFinishedAsana) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startNextAsana();
                        }
                    }, 1000);
                } else { // We just finished the begin audio
                    startTimer(mCurrentAsana.getTime() * 1000);
                }
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
        startNextAsana();
    }

    private void startNextAsana() {
        mCurrentAsana = mAsanas.getByPosition(++mCurrentAsanaPosition);
        mFinishedAsana = false;

        if (mCurrentAsana == null) {
            finish();
        } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
            PracticeActivityFragment fragment = (PracticeActivityFragment) fragmentManager
                    .findFragmentById(R.id.fragment);
            fragment.updateAsana(mCurrentAsana);

            try {
                playBeginAudio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void playBeginAudio() throws IOException {
        mAudioPlayer.setDataSource(this, Uri.parse(AUDIO_URL_START + mCurrentAsana.getAudioBegin()));
        playAudio();
    }

    private void playEndAudio() {
        try {
            mAudioPlayer.setDataSource(this, Uri.parse(AUDIO_URL_START + mCurrentAsana.getAudioEnd()));
            playAudio();
        } catch (IOException e) {
            // TODO handle
            e.printStackTrace();
        }
    }

    private void playAudio() {
        try {
            mAudioPlayer.prepare();
            resumeAudio();
        } catch (IOException e) {
            // TODO handle
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        mAudioPlayer.pause();
        mPracticeState = PRACTICE_STATE_PAUSED;
        mCountdownTimer.cancel();
    }

    private void resumeAudio() throws IOException {
        mAudioPlayer.start();
        mPracticeState = PRACTICE_STATE_PLAYING;
    }

    private void startTimer(long millisRemaining) {
        // Callback occurs 10 times per second
        mCountdownTimer = new CountDownTimer(millisRemaining, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                mCurrentMillisRemaining = millisUntilFinished;
                ((PracticeActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment)).onTick10TimesPerSecond(mCurrentMillisRemaining);
            }

            @Override
            public void onFinish() {
                mFinishedAsana = true;
                playEndAudio();
            }
        }.start();
    }

    private void fabPressed() {
        try {
            if (mPracticeState == PRACTICE_STATE_IDLE) {
                playBeginAudio();
                ((PracticeActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment)).onPracticeStarted();
                mFab.setImageResource(android.R.drawable.ic_media_pause);
            } else if (mPracticeState == PRACTICE_STATE_PLAYING) {
                pauseAudio();
                ((PracticeActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment)).onPracticePaused();
                mFab.setImageResource(android.R.drawable.ic_media_play);
            } else if (mPracticeState == PRACTICE_STATE_PAUSED) {
                if (mAudioPlayer.isPlaying()) {
                    resumeAudio();
                } else {
                    startTimer(mCurrentMillisRemaining);
                }
                ((PracticeActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment)).onPracticeResumed();
                mFab.setImageResource(android.R.drawable.ic_media_pause);
            }
        } catch (IOException e) {
            // TODO Show problem to user
            e.printStackTrace();
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
