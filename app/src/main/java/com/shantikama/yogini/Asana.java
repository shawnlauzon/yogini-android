package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Set;

/**
 * Created by Shawn Lauzon
 */
public class Asana {
    private static final int UNDEFINED = -1;

    private String id;
    private String name;
    private int order;
    private int time;
    private int chakra;

    private String announceAudio;
    private int announcePause;

    private List<SequenceItem> sequence;

    private String stretchAudio;
    private int stretchPause;

    private Asana mParent;

    private Asana() {
        this.id = null;
        this.name = null;
        this.order = UNDEFINED;
        this.time = UNDEFINED;
        this.chakra = UNDEFINED;
        this.announceAudio = null;
        this.announcePause = UNDEFINED;
        this.sequence = null;
        this.stretchAudio = null;
        this.stretchPause = UNDEFINED;
    }

    public void resolveParent(Performance parentPerformance) {
        mParent = parentPerformance.getById(id);
        if (sequence != null) {
            for (int i = 0; i < sequence.size(); i++) {
                sequence.get(i).resolveParent(mParent, i);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void updateId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : (mParent != null ? mParent.getName() : null);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order != UNDEFINED ? order : (mParent != null ? mParent.getOrder() : 0);
    }

    public void updateOrder(int order) {
        this.order = order;
    }

    public int getTime() {
        return time != UNDEFINED ? time : (mParent != null ? mParent.getTime() : 0);
    }

    /**
     * Update the time for this asana, in seconds
     */
    public void updateTime(int time) {
        this.time = time;
    }

    public int getChakra() {
        return chakra != UNDEFINED ? chakra : (mParent != null ? mParent.getChakra() : 0);
    }

    public void updateChakra(int chakra) {
        this.chakra = chakra;
    }

    public String getAnnounceAudio() {
        return announceAudio != null ? announceAudio : (mParent != null ? mParent.getAnnounceAudio() : null);
    }

    public void updateAnnounceAudio(String announceAudio) {
        this.announceAudio = announceAudio;
    }

    public int getAnnouncePause() {
        return announcePause != UNDEFINED ? announcePause : (mParent != null ? mParent.getAnnouncePause() : 0);
    }

    public void updateAnnouncePause(int announcePause) {
        this.announcePause = announcePause;
    }

    public List<SequenceItem> getSequence() {
        return sequence != null ? sequence : (mParent != null ? mParent.getSequence() : null);
    }

    public void updateSequence(ImmutableList<SequenceItem> sequence) {
        this.sequence = sequence;
    }

    public String getStretchAudio() {
        return stretchAudio != null ? stretchAudio : (mParent != null ? mParent.getStretchAudio() : null);
    }

    public void updateStretchAudio(String stretchAudio) {
        this.stretchAudio = stretchAudio;
    }

    public int getStretchPause() {
        return stretchPause != UNDEFINED ? stretchPause : (mParent != null ? mParent.getStretchPause() : 0);
    }

    public void updateStretchPause(int stretchPause) {
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
        public static final String PHASE_TECHNIQUE = "technique";
        public static final String PHASE_CONCENTRATION = "concentration";
        public static final String PHASE_BEGIN = "begin";
        public static final String PHASE_END = "end";
        public static final String PHASE_AWARENESS = "awareness";

        private String techniqueAudio;
        private String concentrationAudio;
        private String awarenessAudio;

        private int techniquePause;
        private int concentrationPause;
        private int beginPause;
        private int endPause;
        private int awarenessPause;

        private int time;

        private Set<String> mSkipPhases;

        private SequenceItem mParent;

        public void resolveParent(Asana parentAsana, int pos) {
            mParent = parentAsana.getSequence().get(pos);
        }

        public String getTechniqueAudio() {
            return techniqueAudio != null ? techniqueAudio :
                    (mParent != null ? mParent.getTechniqueAudio() : null);
        }

        public void updateTechniqueAudio(String techniqueAudio) {
            this.techniqueAudio = techniqueAudio;
        }

        public String getConcentrationAudio() {
            return concentrationAudio != null ? concentrationAudio :
                    (mParent != null ? mParent.getConcentrationAudio() : null);
        }

        public void updateConcentrationAudio(String concentrationAudio) {
            this.concentrationAudio = concentrationAudio;
        }

        public String getAwarenessAudio() {
            return awarenessAudio != null ? awarenessAudio :
                    (mParent != null ? mParent.getAwarenessAudio() : null);
        }

        public void updateAwarenessAudio(String awarenessAudio) {
            this.awarenessAudio = awarenessAudio;
        }

        public int getTechniquePause() {
            return techniquePause != UNDEFINED ? techniquePause :
                    (mParent != null ? mParent.getTechniquePause() : 0);
        }

        public void updateTechniquePause(int techniquePause) {
            this.techniquePause = techniquePause;
        }

        public int getConcentrationPause() {
            return concentrationPause != UNDEFINED ? concentrationPause :
                    (mParent != null ? mParent.getConcentrationPause() : 0);
        }

        public void updateConcentrationPause(int concentrationPause) {
            this.concentrationPause = concentrationPause;
        }

        public int getBeginPause() {
            return beginPause != UNDEFINED ? beginPause :
                    (mParent != null ? mParent.getBeginPause() : 0);

        }

        public void updateBeginPause(int beginPause) {
            this.beginPause = beginPause;
        }

        public int getEndPause() {
            return endPause != UNDEFINED ? endPause :
                    (mParent != null ? mParent.getEndPause() : 0);
        }

        public void updateEndPause(int endPause) {
            this.endPause = endPause;
        }

        public int getAwarenessPause() {
            return awarenessPause != UNDEFINED ? awarenessPause :
                    (mParent != null ? mParent.getAwarenessPause() : 0);

        }

        public void updateAwarenessPause(int awarenessPause) {
            this.awarenessPause = awarenessPause;
        }

        public int getTime() {
            return time != UNDEFINED ? time :
                    (mParent != null ? mParent.getTime() : 0);
        }

        public void updateTime(int time) {
            this.time = time;
        }

        public Set<String> getSkipPhases() {
            return mSkipPhases != null ? mSkipPhases :
                    (mParent != null ? mParent.getSkipPhases() : null);
        }

        public boolean isSkipped(String phase) {
            Set<String> skipPhases = getSkipPhases();
            return skipPhases != null && skipPhases.contains(phase);
        }

        public void updateSkipPhases(Set<String> skipPhases) {
            this.mSkipPhases = skipPhases;
        }
    }
}
