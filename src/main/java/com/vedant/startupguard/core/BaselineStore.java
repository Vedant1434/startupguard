package com.vedant.startupguard.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedant.startupguard.model.StartupMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class BaselineStore {
    private static final Logger logger = LoggerFactory.getLogger(BaselineStore.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String filePath;

    public BaselineStore(String filePath) {
        this.filePath = filePath;
    }

    public StartupMetrics loadBaseline() {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try {
            return objectMapper.readValue(file, StartupMetrics.class);
        } catch (IOException e) {
            logger.error("Failed to load baseline", e);
            return null;
        }
    }

    public void saveBaseline(StartupMetrics metrics) {
        try {
            objectMapper.writeValue(new File(filePath), metrics);
            logger.info("Saved new startup baseline to {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to save baseline", e);
        }
    }
}