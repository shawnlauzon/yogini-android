package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Admin on 100/10/16.
 */
public class Asanas {
    public final String beginAudio;
    public final String endAudio;
    public final String remaining30Sec;
    public final String remaining1Min;
    public ImmutableList<String> chakras;

    public ImmutableList<Asana> asanas;

    public Asanas(String beginAudio, String endAudio, String remaining30Sec, String remaining1Min, ImmutableList<String> chakras, ImmutableList<Asana> asanas) {
        this.beginAudio = beginAudio;
        this.endAudio = endAudio;
        this.remaining30Sec = remaining30Sec;
        this.remaining1Min = remaining1Min;
        this.chakras = chakras;
        this.asanas = asanas;
    }

    public Asana getById(int asanaId) {
        for (Asana a : asanas) {
            if (asanaId == a.id) {
                return a;
            }
        }
        return null;
    }

    public Asana getByPosition(int position) {
        if (asanas.size() - 1 < position) {
            return null;
        }
        return asanas.get(position);
    }

    @Override
    public String toString() {
        return "Asanas{" +
                "asanas=" + asanas +
                '}';
    }
}
