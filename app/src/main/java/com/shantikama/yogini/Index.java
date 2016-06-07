package com.shantikama.yogini;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shawn Lauzon
 */
public class Index {
    private List<PerformanceInfo> performances;

    public Index() {
        this.performances = new ArrayList<>();
    }

    public PerformanceInfo getById(String id) {
        for (PerformanceInfo pi : performances) {
            if (id.equals(pi.id)) {
                return pi;
            }
        }
        return null;
    }

    public List<PerformanceInfo> getPerformances() {
        return performances;
    }

    public void addPerformance(PerformanceInfo pi) {
        performances.add(pi);
    }
}
