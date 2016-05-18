package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Shawn Lauzon
 */
public class Asana {
    public final int id;
    public final String name;
    public final int order;
    public final int time;
    public final int chakra;

    public final String announceAudio;
    public final int announcePause;

    public final ImmutableList<SequenceItem> sequence;

    public final String stretchAudio;
    public final int stretchPause;

    public Asana(int id, String name, int order, int time, int chakra, String announceAudio,
                 int announcePause, ImmutableList<SequenceItem> sequence, String stretchAudio,
                 int stretchPause) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.time = time;
        this.chakra = chakra;
        this.announceAudio = announceAudio;
        this.announcePause = announcePause;
        this.sequence = sequence;
        this.stretchAudio = stretchAudio;
        this.stretchPause = stretchPause;
    }

    @Override
    public String toString() {
        return "Asana {" +
                "id=" + id +
                ", name='" + name +
                '}';
    }

    public static class SequenceItem {
        public final String techniqueAudio;
        public final String concentrationAudio;
        public final String awarenessAudio;

        public final int techniquePause;
        public final int concentrationPause;
        public final int beginPause;
        public final int endPause;
        public final int awarenessPause;

        public final int time;

        public final SkipPhase skip;

        public SequenceItem(String techniqueAudio, String concentrationAudio, String awarenessAudio,
                            int techniquePause, int concentrationPause, int beginPause, int endPause,
                            int awarenessPause, int time, SkipPhase skip) {
            this.techniqueAudio = techniqueAudio;
            this.concentrationAudio = concentrationAudio;
            this.awarenessAudio = awarenessAudio;
            this.techniquePause = techniquePause;
            this.concentrationPause = concentrationPause;
            this.beginPause = beginPause;
            this.endPause = endPause;
            this.awarenessPause = awarenessPause;
            this.time = time;
            this.skip = skip;
        }
    }

    public static class SkipPhase {
        public final boolean technique;
        public final boolean concentration;
        public final boolean begin;
        public final boolean end;
        public final boolean awareness;

        public SkipPhase(boolean technique, boolean concentration, boolean begin, boolean end,
                         boolean awareness) {
            this.technique = technique;
            this.concentration = concentration;
            this.begin = begin;
            this.end = end;
            this.awareness = awareness;
        }
    }
}
