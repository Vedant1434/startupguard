package com.vedant.startupguard;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "startup-guard")
public class StartupGuardProperties {

    private boolean enabled = false;
    private String baselinePath = "startup-baseline.json";
    private double thresholdPercent = 5.0;
    private boolean inspectGc = false;
    private boolean generateHtml = false;
    private String focusPackage = null;
    private boolean smartHardwareCheck = false;

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getBaselinePath() { return baselinePath; }
    public void setBaselinePath(String baselinePath) { this.baselinePath = baselinePath; }
    public double getThresholdPercent() { return thresholdPercent; }
    public void setThresholdPercent(double thresholdPercent) { this.thresholdPercent = thresholdPercent; }

    public boolean isInspectGc() { return inspectGc; }
    public void setInspectGc(boolean inspectGc) { this.inspectGc = inspectGc; }
    public boolean isGenerateHtml() { return generateHtml; }
    public void setGenerateHtml(boolean generateHtml) { this.generateHtml = generateHtml; }
    public String getFocusPackage() { return focusPackage; }
    public void setFocusPackage(String focusPackage) { this.focusPackage = focusPackage; }
    public boolean isSmartHardwareCheck() { return smartHardwareCheck; }
    public void setSmartHardwareCheck(boolean smartHardwareCheck) { this.smartHardwareCheck = smartHardwareCheck; }
}