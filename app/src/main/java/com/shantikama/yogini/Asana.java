package com.shantikama.yogini;

/**
 * Created by Admin on 5/10/16.
 */
public class Asana {
    public final int id;
    public final String name;
    public final int order;
    public final int time;
    public final String audio_begin;
    public final String audio_end;
    public final SequenceItem[] sequence;

    public Asana(int id, String name, int order, int time, String audio_begin, String audio_end, SequenceItem[] sequence) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.time = time;
        this.audio_begin = audio_begin;
        this.audio_end = audio_end;
        this.sequence = sequence;
    }

   @Override
    public String toString() {
        return "Asana{" +
                "id=" + id +
                ", name='" +
                '}';
    }

    public static class SequenceItem {
        private String audio;
        private int pause;
    }
}
