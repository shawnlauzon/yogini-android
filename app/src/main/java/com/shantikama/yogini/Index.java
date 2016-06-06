package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;
import com.shantikama.yogini.ui.PracticeInfo;

/**
 * Created by Admin on 100/10/16.
 */
public class Index {
    public ImmutableList<PracticeInfo> practices;

    public Index(ImmutableList<PracticeInfo> practices) {
        this.practices = practices;
    }
}
