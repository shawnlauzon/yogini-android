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
    private final Map<String, Asanas> mAllPractices = new HashMap<>();

    public static JsonLibrary getInstance() {
        return sInstance;
    }

    public Index getIndex(Context context) {
        if (mIndex == null) {
            mIndex = GsonUtils.deserialize(context, R.raw.index, Index.class);
        }
        return mIndex;
    }

    public Asanas getAsanas(Context context, String practiceId) {
        if (!mAllPractices.containsKey(practiceId)) {
            int jsonResId = GsonUtils.getRawResId(context, mIndex.getById(practiceId).json);
            Asanas asanas = GsonUtils.deserialize(context, jsonResId, Asanas.class);
            asanas.resolveParent(context);
            mAllPractices.put(practiceId, asanas);
        }
        return mAllPractices.get(practiceId);
    }
}
