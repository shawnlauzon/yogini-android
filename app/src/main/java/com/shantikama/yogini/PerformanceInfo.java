package com.shantikama.yogini;

/**
 * Created by Shawn Lauzon
 */
public class PerformanceInfo {
    public final String id;
    public final String name;
    public final int timeMinutes;
    public final String json;

    public PerformanceInfo(String id, String name, int timeMinutes, String json) {
        this.id = id;
        this.name = name;
        this.timeMinutes = timeMinutes;
        this.json = json;
    }
}
