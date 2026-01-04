package com.vedant.startupguard.core;

import com.vedant.startupguard.model.RegressionReport;
import com.vedant.startupguard.model.StartupMetrics;

import java.util.Map;

public class StartupAnalyzer {

    private final double thresholdPercent;

    public StartupAnalyzer(double thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    public RegressionReport analyze(StartupMetrics current, StartupMetrics baseline) {
        RegressionReport report = new RegressionReport();

        if (baseline == null) {
            report.setSlow(false);
            return report;
        }

        long diff = current.getTotalStartupTimeMs() - baseline.getTotalStartupTimeMs();
        double changePercent = (double) diff / baseline.getTotalStartupTimeMs() * 100;

        // 1. Check if the overall app is too slow
        if (changePercent > thresholdPercent) {
            report.setSlow(true);
            report.setDelayMs(diff);

            Map<String, Long> baselineBeans = baseline.getBeanInitTimesMs();

            // 2. Iterate through CURRENT beans to find the culprit
            current.getBeanInitTimesMs().forEach((beanName, currentTime) -> {
                Long baselineTime = baselineBeans.get(beanName);

                if (baselineTime != null) {
                    // CASE A: An EXISTING bean got slower
                    long allowedTime = (long) (baselineTime * (1 + (thresholdPercent / 100)));
                    if (currentTime > allowedTime) {
                        long slowdown = currentTime - baselineTime;
                        // Only report if the slowdown is significant (> 10ms) to reduce noise
                        if (slowdown > 10) {
                            report.getSlowBeans().add(beanName + " (Slower by " + slowdown + "ms)");
                        }
                    }
                } else {
                    // CASE B: A NEW bean (not in baseline) is causing the delay
                    // If a new bean takes more than 100ms, we assume it's a "Heavy New Dependency"
                    if (currentTime > 100) {
                        report.getSlowBeans().add(beanName + " (New Bean: " + currentTime + "ms)");
                    }
                }
            });
        }
        return report;
    }
}