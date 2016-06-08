package com.shantikama.yogini;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.shantikama.yogini.utils.GsonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Shawn Lauzon
 */
public class JsonLibrary {
    private static final String TAG = "JsonLibrary";

    public static final String PERFORMANCE_ID_ALL_ASANAS = "76738951-a913-4b3d-a902-dc2e03c0c47c";

    private static final JsonLibrary sInstance = new JsonLibrary();

    private static final String INDEX_FILENAME = "index.json";

    private Index mIndex;
    private Index mLocalIndex;

    /**
     * Performances which have currently been loaded, keyed by id.
     */
    private final Map<String, Performance> mLoadedPerformances = new HashMap<>();

    public static JsonLibrary getInstance() {
        return sInstance;
    }

    private Index getIndex(Context context) {
        if (mIndex == null) {
            mIndex = GsonUtils.deserialize(context, R.raw.index, Index.class);
        }
        return mIndex;
    }

    private Index getLocalIndex(Context context) {
        if (mLocalIndex == null) {
            try {
                mLocalIndex = load(context, INDEX_FILENAME, Index.class);
            } catch (FileNotFoundException e) {
                mLocalIndex = createLocalIndex(context);
            }
        }
        return mLocalIndex;
    }

    public List<PerformanceInfo> getPerformances(Context context) {
        return Lists.newArrayList(Iterables.concat(getLocalIndex(context).getPerformances(),
                getIndex(context).getPerformances()));
//        return new ImmutableList.Builder<PerformanceInfo>()
//                .addAll(getLocalIndex(context).getPerformances())
//                .addAll(getIndex(context).getPerformances())
//                .build();
    }

    private static Index createLocalIndex(Context context) {
        Index index = new Index();
        new File(context.getFilesDir(), INDEX_FILENAME);
        save(context, INDEX_FILENAME, index);
        return index;
    }

    public static <T> T load(Context context, String filename, Class<T> clazz) throws FileNotFoundException {
        FileInputStream inputStream;
        InputStreamReader inputStreamReader;

        inputStream = context.openFileInput(filename);
        inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        T o = GsonUtils.GSON.fromJson(inputStreamReader, clazz);
        try {
            inputStreamReader.close();
        } catch (IOException e) {
            Log.w(TAG, "Error closing " + filename, e);
        }
        return o;
    }

    public static void save(Context context, String filename, Object object) {
        FileOutputStream outputStream;
        OutputStreamWriter outputStreamWriter;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            new File(context.getFilesDir(), filename);
            try {
                outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "Could not write to file " + filename, e2);
                return;
            }
        }
        outputStreamWriter = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
        GsonUtils.GSON.toJson(object, outputStreamWriter);
        try {
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.w(TAG, "Failure flushing & closing " + filename, e);
        }
    }

    public Performance getPerformance(Context context, String performanceId) {
        if (!mLoadedPerformances.containsKey(performanceId)) {
            Performance asanas;
            PerformanceInfo pi = getIndex(context).getById(performanceId);
            if (pi != null) {
                asanas = GsonUtils.deserialize(context, pi.getResId(context), Performance.class);
            } else {
                pi = mLocalIndex.getById(performanceId);
                if (pi == null) {
                    throw new NoSuchElementException();
                }
                try {
                    asanas = load(context, pi.getFilename(), Performance.class);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Could not find file in index " + pi.getFilename());
                    throw Throwables.propagate(e);
                }
            }
            asanas.resolveParent(context);
            mLoadedPerformances.put(performanceId, asanas);
        }
        return mLoadedPerformances.get(performanceId);
    }

    public void addPerformance(Context context, Performance performance) {
        mLocalIndex.addPerformance(performance.newPerformanceInfo());
        save(context, INDEX_FILENAME, mLocalIndex);
        mLoadedPerformances.put(performance.getId(), performance);
    }

    public PerformanceInfo removePerformanceWithItemId(Context context, long l) {
        for (ListIterator<PerformanceInfo> iterator = mLocalIndex.getPerformances().listIterator(); iterator.hasNext();) {
            PerformanceInfo pi = iterator.next();
            if (l == pi.getItemId()) {
                iterator.remove();
                save(context, INDEX_FILENAME, mLocalIndex);
                new File(context.getFilesDir(), pi.getFilename());
                mLoadedPerformances.remove(pi.id);
                return pi;
            }
        }
        return null;
    }
}
