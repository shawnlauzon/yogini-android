package com.shantikama.yogini;

import com.google.common.base.Optional;
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
    public final Optional<ImmutableList<SequenceItem>> multiPart;
    public final Optional<PolarAsana> polarAsana;

    public Asana(int id, String name, int order, int time, String audioBegin, String audioEnd,
                 Optional<ImmutableList<SequenceItem>> multiPart, Optional<PolarAsana> polarAsana) {
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

    @Override
    public String toString() {
        return "Asana {" +
                "id=" + id +
                ", name='" + name +
                '}';
    }

    public static class SequenceItem {
        public final String audio;
        public final int pause;

        public SequenceItem(String audio, int pause) {
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
