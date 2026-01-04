package com.vedant.startupguard.model;

import java.util.HashMap;
import java.util.Map;

public class StartupMetrics {
    private long totalStartupTimeMs;
    private long usedHeapMemoryBytes;
    private int loadedClassCount;

    // Hardware Specs (for Smart Check)
    private int availableProcessors;
    private long maxHeapMemoryBytes;

    private Map<String, Long> beanInitTimesMs = new HashMap<>();

    // Standard Getters and Setters
    public long getTotalStartupTimeMs() { return totalStartupTimeMs; }
    public void setTotalStartupTimeMs(long totalStartupTimeMs) { this.totalStartupTimeMs = totalStartupTimeMs; }

    public long getUsedHeapMemoryBytes() { return usedHeapMemoryBytes; }
    public void setUsedHeapMemoryBytes(long usedHeapMemoryBytes) { this.usedHeapMemoryBytes = usedHeapMemoryBytes; }

    public int getLoadedClassCount() { return loadedClassCount; }
    public void setLoadedClassCount(int loadedClassCount) { this.loadedClassCount = loadedClassCount; }

    public int getAvailableProcessors() { return availableProcessors; }
    public void setAvailableProcessors(int availableProcessors) { this.availableProcessors = availableProcessors; }

    public long getMaxHeapMemoryBytes() { return maxHeapMemoryBytes; }
    public void setMaxHeapMemoryBytes(long maxHeapMemoryBytes) { this.maxHeapMemoryBytes = maxHeapMemoryBytes; }

    public Map<String, Long> getBeanInitTimesMs() { return beanInitTimesMs; }
    public void setBeanInitTimesMs(Map<String, Long> beanInitTimesMs) { this.beanInitTimesMs = beanInitTimesMs; }
}