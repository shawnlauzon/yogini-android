package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Admin on 5/10/16.
 */
public class Asana {
    public final int id;
    public final String name;
    public final int order;
    public final int time;
    public final String audioBegin;
    public final String audioEnd;
    public final ImmutableList<AsanaPart> multiPart;
    public final PolarAsana polarAsana;

    public Asana(int id, String name, int order, int time, String audioBegin, String audioEnd,
                 ImmutableList<AsanaPart> multiPart, PolarAsana polarAsana) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.time = time;
        this.audioBegin = audioBegin;
        this.audioEnd = audioEnd;
        this.multiPart = multiPart;
        this.polarAsana = polarAsana;
    }

    public boolean isMultiPart() {
        return multiPart != null;
    }

    public boolean isPolarAsana() {
        return polarAsana != null;
    }

    @Override
    public String toString() {
        return "Asana {" +
                "id=" + id +
                ", name='" + name +
                '}';
    }

    public static class AsanaPart {
        public final String audio;
        public final int pause;

        public AsanaPart(String audio, int pause) {
            this.audio = audio;
            this.pause = pause;
        }
    }

    public static class PolarAsana {
        public final String leftBegin;
        public final String leftEnd;
        public final String rightBegin;
        public final String rightEnd;

        public PolarAsana(String leftBegin, String leftEnd, String rightBegin, String rightEnd) {
            this.leftBegin = leftBegin;
            this.leftEnd = leftEnd;
            this.rightBegin = rightBegin;
            this.rightEnd = rightEnd;
        }
    }
}
