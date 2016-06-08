package com.shantikama.yogini;

import android.content.Context;

import java.util.UUID;

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

    public long getItemId() {
        return UUID.fromString(id).getLeastSignificantBits();
    }

    public static String getFilename(String id) {
        return "ap" + id.replaceAll("-", "") + ".json";
    }

    public String getFilename() {
        return getFilename(this.id);
    }

    public static int getResId(Context context, String id) {
        return context.getResources().getIdentifier("ap" + id.replaceAll("-", ""), "raw", context.getPackageName());
    }

    public int getResId(Context context) {
        return getResId(context, this.id);
    }

}
