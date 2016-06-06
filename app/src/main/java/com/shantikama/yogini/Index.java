package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Admin on 100/10/16.
 */
public class Index {
    public ImmutableList<PracticeInfo> practices;

    public Index(ImmutableList<PracticeInfo> practices) {
        this.practices = practices;
    }

    public PracticeInfo getById(String id) {
        for (PracticeInfo pi : practices) {
            if (id.equals(pi.id)) {
                return pi;
            }
        }
        return null;
    }
}
