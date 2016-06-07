package com.shantikama.yogini;

import android.content.Context;

import com.google.common.collect.ImmutableList;

import java.util.UUID;

/**
 * Created by Shawn Lauzon.
 */
public class Performance {
    private String name;
    private String id;
    private boolean published;

    private String beginAudio;
    private String endAudio;
    private String remaining30Sec;
    private String remaining1Min;
    private int timeMinutes;
    private String parent;

    private ImmutableList<Asana> asanas;

    private Performance mParent;

    private Performance() {
        this.name = null;
        this.id = null;
        this.published = false;
        this.beginAudio = null;
        this.endAudio = null;
        this.remaining30Sec = null;
        this.remaining1Min = null;
        this.timeMinutes = -1;
        this.parent = null;
        this.asanas = null;
    }

    public Asana getById(String asanaId) {
        for (Asana a : asanas) {
            if (asanaId.equals(a.getId())) {
                return a;
            }
        }
        return null;
    }

    public void resolveParent(Context context) {
        if (parent != null) {
            mParent = JsonLibrary.getInstance().getPerformance(context, parent);
            for (Asana a : asanas) {
                a.resolveParent(mParent);
            }
        }
    }

    public PerformanceInfo newPerformanceInfo() {
        return new PerformanceInfo(id, name, 0, id.replaceAll("-", ""));
    }

    public void save(Context context) {
        if (published) {
            throw new IllegalStateException("Cannot store published performances");
        }

        JsonLibrary.getInstance().save(context, id.replaceAll("-", "") + ".json", this);
    }

    public String saveNew(Context context) {
        id = UUID.randomUUID().toString();
        published = false;
        save(context);
        JsonLibrary.getInstance().addPerformance(context, this);
        return id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : mParent.getName();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public boolean isPublished() {
        return published;
    }

    public String getBeginAudio() {
        return beginAudio != null ? beginAudio : (mParent != null ? mParent.getBeginAudio() : null);
    }

    public String getEndAudio() {
        return endAudio != null ? endAudio : (mParent != null ? mParent.getEndAudio() : null);
    }

    public String getRemaining30Sec() {
        return remaining30Sec != null ? remaining30Sec : (mParent != null ? mParent.getRemaining30Sec() : null);
    }

    public String getRemaining1Min() {
        return remaining1Min != null ? remaining1Min : (mParent != null ? mParent.getRemaining1Min() : null);
    }

    public int getTimeMinutes() {
        return timeMinutes != -1 ? timeMinutes : (mParent != null ? mParent.getTimeMinutes() : 0);
    }

    public String getParent() {
        return parent;
    }

    public ImmutableList<Asana> getAsanas() {
        return asanas != null ? asanas : mParent.getAsanas();
    }

    public void setAsanas(ImmutableList<Asana> asanas) {
        this.asanas = asanas;
    }
}
