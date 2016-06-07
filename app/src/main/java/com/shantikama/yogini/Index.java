package com.shantikama.yogini;

import com.google.common.collect.ImmutableList;

/**
 * Created by Shawn Lauzon
 */
public class Index {
    private ImmutableList<PerformanceInfo> performances;

    private Index() {
        this.performances = null;
    }

    public PerformanceInfo getById(String id) {
        for (PerformanceInfo pi : performances) {
            if (id.equals(pi.id)) {
                return pi;
            }
        }
        return null;
    }

    public PerformanceInfo get(int position) {
        return getPerformances().get(position);
    }

    public ImmutableList<PerformanceInfo> getPerformances() {
        return performances;
    }
}
