package com.vedant.startupguard.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanInitTracker implements BeanPostProcessor {

    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> durations = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        startTimes.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Long start = startTimes.remove(beanName);
        if (start != null) {
            long duration = System.currentTimeMillis() - start;
            if (duration > 1) {
                // FIX: Include the Class Name in the key so we can filter by package later
                String key = beanName + " [" + bean.getClass().getName() + "]";
                durations.put(key, duration);
            }
        }
        return bean;
    }

    public Map<String, Long> getDurations() {
        return durations;
    }
}