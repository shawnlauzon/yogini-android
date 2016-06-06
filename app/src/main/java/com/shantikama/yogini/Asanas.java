package com.shantikama.yogini;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.shantikama.yogini.utils.GsonUtils;

/**
 * Created by Admin on 100/10/16.
 */
public class Asanas {
    public final String name;
    public final String beginAudio;
    public final String endAudio;
    public final String remaining30Sec;
    public final String remaining1Min;
    public ImmutableList<String> chakras;
    public final int timeMinutes;
    public final String parent;

    public ImmutableList<Asana> asanas;

    private Asanas() {
        this.name = null;
        this.beginAudio = null;
        this.endAudio = null;
        this.remaining30Sec = null;
        this.remaining1Min = null;
        this.timeMinutes = -1;
        this.parent = null;
        this.chakras = null;
        this.asanas = null;
    }

    public Asanas(String name, String beginAudio, String endAudio, String remaining30Sec, String remaining1Min,
                  ImmutableList<String> chakras, int timeMinutes, String parent,
                  ImmutableList<Asana> asanas) {
        this.name = name;
        this.beginAudio = beginAudio;
        this.endAudio = endAudio;
        this.remaining30Sec = remaining30Sec;
        this.remaining1Min = remaining1Min;
        this.timeMinutes = timeMinutes;
        this.parent = parent;
        this.chakras = chakras;
        this.asanas = asanas;
    }

    public Asana getById(String asanaId) {
        for (Asana a : asanas) {
            if (asanaId.equals(a.id)) {
                return a;
            }
        }
        return null;
    }

    public Asanas newInstanceWithResolvedParent(Context context) {
        if (parent == null) {
            return this;
        } else {
            return newInstanceWithResolvedParent(GsonUtils.deserialize(context,
                    GsonUtils.getRawResId(context, parent), Asanas.class));
        }
    }

    public Asanas newInstanceWithResolvedParent(Asanas parentAsanas) {
        return new Asanas(
                name == null ? parentAsanas.name : name,
                beginAudio == null ? parentAsanas.beginAudio : beginAudio,
                endAudio == null ? parentAsanas.endAudio : endAudio,
                remaining30Sec == null ? parentAsanas.remaining30Sec : remaining30Sec,
                remaining1Min == null ? parentAsanas.remaining1Min : remaining1Min,
                chakras == null ? parentAsanas.chakras : chakras,
                timeMinutes == -1 ? parentAsanas.timeMinutes : timeMinutes,
                parent == null ? parentAsanas.parent : parent,
                asanas == null ? parentAsanas.asanas : newListWithResolvedParent(parentAsanas));
    }

    private ImmutableList<Asana> newListWithResolvedParent(Asanas parentAsanas) {
        ImmutableList.Builder<Asana> builder = new ImmutableList.Builder<>();
        for (Asana a : asanas) {
            builder.add(a.newInstanceWithResolvedParent(parentAsanas.getById(a.id)));
        }
        return builder.build();
    }

}
