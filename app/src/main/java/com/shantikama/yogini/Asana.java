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
    public final int chakra;

    public final String announceAudio;
    public final int announcePause;

    public final ImmutableList<SequenceItem> sequence;

    public Asana(int id, String name, int order, int time, int chakra, ImmutableList<SequenceItem> sequence, String announceAudio, int announcePause) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.time = time;
        this.chakra = chakra;
        this.sequence = sequence;
        this.announceAudio = announceAudio;
        this.announcePause = announcePause;
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

    public static class SequenceItem {
        public final String techniqueAudio;
        public final String concentrationAudio;
        public final String awarenessAudio;

        public final int techniquePause;
        public final int concentrationPause;
        public final int endPause;
        public final int awarenessPause;

        public final int time;

        public SequenceItem(String techniqueAudio, String concentrationAudio, String awarenessAudio, int techniquePause, int concentrationPause, int endPause, int awarenessPause, int time) {
            this.techniqueAudio = techniqueAudio;
            this.concentrationAudio = concentrationAudio;
            this.awarenessAudio = awarenessAudio;
            this.techniquePause = techniquePause;
            this.concentrationPause = concentrationPause;
            this.endPause = endPause;
            this.awarenessPause = awarenessPause;
            this.time = time;
        }
    }
}
