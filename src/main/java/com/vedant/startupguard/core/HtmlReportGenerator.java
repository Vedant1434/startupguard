package com.vedant.startupguard.core;

import com.vedant.startupguard.model.StartupMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HtmlReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HtmlReportGenerator.class);

    public void generate(StartupMetrics metrics, boolean isSlow, String filePath) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: sans-serif; padding: 20px; background: #f4f4f9; }");
        html.append(".card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }");
        html.append("h1 { color: #333; }");
        html.append(".status { padding: 10px; color: white; border-radius: 4px; display: inline-block; }");
        html.append(".slow { background: #e74c3c; } .fast { background: #2ecc71; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append("</style></head><body>");

        html.append("<h1>Startup Report</h1>");

        // Status Badge
        String statusClass = isSlow ? "slow" : "fast";
        String statusText = isSlow ? "SLOW STARTUP DETECTED" : "STARTUP HEALTHY";
        html.append("<div class='status " + statusClass + "'>" + statusText + "</div>");

        // Metrics Card
        html.append("<div class='card'><h2>Overview</h2>");
        html.append("<p><strong>Time:</strong> " + metrics.getTotalStartupTimeMs() + " ms</p>");
        html.append("<p><strong>Memory:</strong> " + (metrics.getUsedHeapMemoryBytes() / 1024 / 1024) + " MB</p>");
        html.append("<p><strong>Classes:</strong> " + metrics.getLoadedClassCount() + "</p>");
        html.append("</div>");

        // Top Beans Card
        html.append("<div class='card'><h2>Slowest Components</h2><table>");
        html.append("<tr><th>Bean Name</th><th>Time (ms)</th></tr>");

        metrics.getBeanInitTimesMs().entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    html.append("<tr><td>").append(entry.getKey()).append("</td>");
                    html.append("<td>").append(entry.getValue()).append("</td></tr>");
                });

        html.append("</table></div></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
            logger.info("📄 HTML Report generated: " + filePath);
        } catch (IOException e) {
            logger.error("Failed to generate HTML report", e);
        }
    }
}