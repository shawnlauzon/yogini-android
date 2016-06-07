package com.shantikama.yogini;

import android.content.Context;

import com.shantikama.yogini.utils.GsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 6/5/16.
 */
public class JsonLibrary {
    private static final JsonLibrary sInstance = new JsonLibrary();

    private Index mIndex;
    private final Map<String, Performance> mAllPerformances = new HashMap<>();

    public static JsonLibrary getInstance() {
        return sInstance;
    }

    public Index getIndex(Context context) {
        if (mIndex == null) {
            mIndex = GsonUtils.deserialize(context, R.raw.index, Index.class);
        }
        return mIndex;
    }

    public Performance getPerformance(Context context, String practiceId) {
        if (!mAllPerformances.containsKey(practiceId)) {
            int jsonResId = GsonUtils.getRawResId(context, mIndex.getById(practiceId).json);
            Performance asanas = GsonUtils.deserialize(context, jsonResId, Performance.class);
            asanas.resolveParent(context);
            mAllPerformances.put(practiceId, asanas);
        }
        return mAllPerformances.get(practiceId);
    }
}
