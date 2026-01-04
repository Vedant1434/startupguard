package com.vedant.startupguard.model;

import java.util.ArrayList;
import java.util.List;

public class RegressionReport {
    private boolean isSlow;
    private long delayMs;
    private List<String> slowBeans = new ArrayList<>();

    public boolean isSlow() { return isSlow; }
    public void setSlow(boolean slow) { isSlow = slow; }

    public long getDelayMs() { return delayMs; }
    public void setDelayMs(long delayMs) { this.delayMs = delayMs; }

    public List<String> getSlowBeans() { return slowBeans; }
    public void setSlowBeans(List<String> slowBeans) { this.slowBeans = slowBeans; }
}