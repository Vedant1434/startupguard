package com.vedant.startupguard.core;

import com.vedant.startupguard.StartupGuardProperties;
import com.vedant.startupguard.model.RegressionReport;
import com.vedant.startupguard.model.StartupMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

public class StartupGuardListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupGuardListener.class);

    private final BeanInitTracker tracker;
    private final BaselineStore store;
    private final StartupAnalyzer analyzer;
    private final StartupGuardProperties properties;
    private final HtmlReportGenerator htmlGenerator = new HtmlReportGenerator();

    public StartupGuardListener(BeanInitTracker tracker,
                                BaselineStore store,
                                StartupAnalyzer analyzer,
                                StartupGuardProperties properties) {
        this.tracker = tracker;
        this.store = store;
        this.analyzer = analyzer;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 1. Gather Metrics
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        int classCount = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();

        // Hardware Specs
        int cores = Runtime.getRuntime().availableProcessors();
        long maxMem = Runtime.getRuntime().maxMemory();

        // 2. [FEATURE] Inspect GC
        if (properties.isInspectGc()) {
            inspectGarbageCollection();
        }

        StartupMetrics current = new StartupMetrics();
        current.setTotalStartupTimeMs(uptime);
        current.setUsedHeapMemoryBytes(heapUsed);
        current.setLoadedClassCount(classCount);
        current.setAvailableProcessors(cores);
        current.setMaxHeapMemoryBytes(maxMem);
        current.setBeanInitTimesMs(tracker.getDurations());

        StartupMetrics baseline = store.loadBaseline();

        if (baseline == null) {
            logger.info("🆕 First run! Establishing baseline.");
            store.saveBaseline(current);
        } else {
            // 3. [FEATURE] Smart Hardware Check
            if (properties.isSmartHardwareCheck()) {
                if (isHardwareChanged(current, baseline)) {
                    logger.warn("💻 Hardware change detected (Cores: {}->{}, Mem: {}->{}). Resetting baseline.",
                            baseline.getAvailableProcessors(), current.getAvailableProcessors(),
                            baseline.getMaxHeapMemoryBytes(), current.getMaxHeapMemoryBytes());
                    store.saveBaseline(current);
                    return; // Skip regression check this time
                }
            }

            // 4. Analyze & Report
            RegressionReport report = analyzer.analyze(current, baseline);

            // 5. [FEATURE] Focus Package Printing
            printSlowestBeans(current);

            boolean isSlow = report.isSlow();
            if (isSlow) {
                logger.error("🚨 SLOW STARTUP: +{}ms slower than baseline.", report.getDelayMs());
            } else {
                logger.info("✅ Startup Healthy: {}ms", uptime);
                if (uptime < baseline.getTotalStartupTimeMs()) {
                    logger.info("🚀 New personal best! Updating baseline.");
                    store.saveBaseline(current);
                }
            }

            // 6. [FEATURE] HTML Generation
            if (properties.isGenerateHtml()) {
                htmlGenerator.generate(current, isSlow, "startup-report.html");
            }
        }
    }

    private boolean isHardwareChanged(StartupMetrics current, StartupMetrics baseline) {
        // If cores changed OR max memory changed by more than 20%
        if (current.getAvailableProcessors() != baseline.getAvailableProcessors()) return true;

        long memDiff = Math.abs(current.getMaxHeapMemoryBytes() - baseline.getMaxHeapMemoryBytes());
        return memDiff > (baseline.getMaxHeapMemoryBytes() * 0.2);
    }

    private void inspectGarbageCollection() {
        logger.info("--- 🗑️ GC Inspector ---");
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : gcBeans) {
            if (gc.getCollectionCount() > 0) {
                logger.info("   [{}] ran {} times ({}ms)", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
            }
        }
        logger.info("-----------------------");
    }

    private void printSlowestBeans(StartupMetrics metrics) {
        String filterPkg = properties.getFocusPackage();
        logger.info("--- 🐢 Heaviest Components ---");
        if (filterPkg != null && !filterPkg.isEmpty()) {
            logger.info("(Filtering by package: '{}')", filterPkg);
        }

        metrics.getBeanInitTimesMs().entrySet().stream()
                // Apply Focus Filter
                .filter(entry -> filterPkg == null || entry.getKey().toLowerCase().contains(filterPkg.toLowerCase()))
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry -> logger.info("   [{}] took {}ms", entry.getKey(), entry.getValue()));
        logger.info("------------------------------");
    }
}