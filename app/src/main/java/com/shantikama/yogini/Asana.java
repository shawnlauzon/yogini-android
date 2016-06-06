package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Shawn Lauzon
 */
public class Asana {
    public final String id;
    public final String name;
    public final int order;
    public final int time;
    public final int chakra;

    public final String announceAudio;
    public final int announcePause;

    public final ImmutableList<SequenceItem> sequence;

    public final String stretchAudio;
    public final int stretchPause;

    private Asana() {
        this.id = null;
        this.name = null;
        this.order = -1;
        this.time = -1;
        this.chakra = -1;
        this.announceAudio = null;
        this.announcePause = -1;
        this.sequence = null;
        this.stretchAudio = null;
        this.stretchPause = -1;
    }

    public Asana(String id, String name, int order, int time, int chakra, String announceAudio,
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

    public Asana newInstanceWithResolvedParent(Asana parent) {
        return new Asana(id,
                name == null ? parent.name : name,
                order == -1 ? parent.order : order,
                time == -1 ? parent.time : time,
                chakra == -1 ? parent.chakra : chakra,
                announceAudio == null ? parent.announceAudio : announceAudio,
                announcePause == -1 ? parent.announcePause : announcePause,
                sequence == null ? parent.sequence : newListWithResolvedParent(parent.sequence),
                stretchAudio == null ? parent.stretchAudio : stretchAudio,
                stretchPause == -1 ? parent.stretchPause : stretchPause
        );
    }

    private ImmutableList<SequenceItem> newListWithResolvedParent(ImmutableList<SequenceItem> parentSeq) {
        ImmutableList.Builder<SequenceItem> builder = new ImmutableList.Builder<>();
        for (int i = 0; i < sequence.size(); i++) {
            builder.add(sequence.get(i).newInstanceWithResolvedParent(parentSeq.get(i)));
        }
        return builder.build();
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

        private SequenceItem() {
            this.techniqueAudio = null;
            this.concentrationAudio = null;
            this.awarenessAudio = null;
            this.techniquePause = -1;
            this.concentrationPause = -1;
            this.beginPause = -1;
            this.endPause = -1;
            this.awarenessPause = -1;
            this.time = -1;
            this.skip = null;
        }

        public SequenceItem(String techniqueAudio, String concentrationAudio, String awarenessAudio,
                            int techniquePause, int concentrationPause, int beginPause,
                            int endPause, int awarenessPause, int time, SkipPhase skip) {
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

        public SequenceItem newInstanceWithResolvedParent(SequenceItem parent) {
            return new SequenceItem(
                    techniqueAudio == null ? parent.techniqueAudio : techniqueAudio,
                    concentrationAudio == null ? parent.concentrationAudio : concentrationAudio,
                    awarenessAudio == null ? parent.awarenessAudio : awarenessAudio,
                    techniquePause == -1 ? parent.techniquePause : techniquePause,
                    concentrationPause == -1 ? parent.concentrationPause : concentrationPause,
                    beginPause == -1 ? parent.beginPause : beginPause,
                    endPause == -1 ? parent.endPause : endPause,
                    awarenessPause == -1 ? parent.awarenessPause : awarenessPause,
                    time == -1 ? parent.time : time,
                    skip == null ? parent.skip : skip.newInstanceWithResolvedParent(parent.skip)
            );
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
            this.technique = false;
            this.concentration = false;
            this.begin = false;
            this.end = false;
            this.awareness = false;
        }

        public SkipPhase newInstanceWithResolvedParent(SkipPhase parent) {
            return new SkipPhase(
                    technique == false ? parent.technique : technique,
                    concentration == false ? parent.concentration : concentration,
                    begin == false ? parent.begin : begin,
                    end == false ? parent.end : end,
                    awareness == false ? parent.awareness : awareness
            );
        }
    }
}
