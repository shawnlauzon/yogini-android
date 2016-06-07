package com.shantikama.yogini;

import android.content.Context;

import com.shantikama.yogini.utils.GsonUtils;

/**
 * Created by Shawn Lauzon
 */
public class PerformanceInfo {
    public final String id;
    public final String name;
    public final int timeMinutes;

    public PerformanceInfo(String id, String name, int timeMinutes) {
        this.id = id;
        this.name = name;
        this.timeMinutes = timeMinutes;
    }

    public static String idToFilename(String id) {
        return "ap" + id.replaceAll("-", "") + ".json";
    }

    public String idToFilename() {
        return idToFilename(this.id);
    }

    public static int idToResId(Context context, String id) {
        return GsonUtils.getRawResId(context, "ap" + id.replaceAll("-", ""));
    }

    public int idToResId(Context context) {
        return idToResId(context, this.id);
    }

}
