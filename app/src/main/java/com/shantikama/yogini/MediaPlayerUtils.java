package com.shantikama.yogini;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

import com.google.common.base.Throwables;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MediaPlayerUtils {
    /**
     * Create a MediaPlayer which doesn't give the error
     * E/MediaPlayer: Should have subtitle controller already set
     *
     * @return a new MediaPlayer object
     * @link http://stackoverflow.com/a/20149754/355039
     */
    public static MediaPlayer newMediaPlayer(Context context) {

        MediaPlayer mediaplayer = new MediaPlayer();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");

            Constructor constructor = cSubtitleController.getConstructor(Context.class,
                    cMediaTimeProvider, iSubtitleControllerListener);

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
        } catch (Exception e) {
            Throwables.propagate(e);
        }

        return mediaplayer;
    }
}