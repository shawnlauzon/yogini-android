package com.shantikama.yogini;

import java.util.List;

/**
 * Created by Admin on 100/10/16.
 */
public class Asanas {
    private List<Asana> asanas;

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
