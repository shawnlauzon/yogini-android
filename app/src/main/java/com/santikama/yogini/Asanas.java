package com.santikama.yogini;

import java.util.List;

/**
 * Created by Admin on 100/10/16.
 */
public class Asanas {
    private List<Asana> asanas;

    public Asana get(int asanaId) {
        for (Asana a : asanas) {
            if (asanaId == a.getId()) {
                return a;
            }
        }
        return null;
    }

    public List<Asana> getAsanas() {
        return asanas;
    }

    @Override
    public String toString() {
        return "Asanas{" +
                "asanas=" + asanas +
                '}';
    }
}
