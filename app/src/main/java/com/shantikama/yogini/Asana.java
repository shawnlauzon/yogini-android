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
    public final ImmutableList<SequenceItem> multiPart;

    public Asana(int id, String name, int order, int time, String audioBegin, String audioEnd,
                 ImmutableList<SequenceItem> multiPart) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.time = time;
        this.audioBegin = audioBegin;
        this.audioEnd = audioEnd;
        this.multiPart = multiPart;
    }

   @Override
    public String toString() {
        return "Asana {" +
                "id=" + id +
                ", name='" +
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
}
