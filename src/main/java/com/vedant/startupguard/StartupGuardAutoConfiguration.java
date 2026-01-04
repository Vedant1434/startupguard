package com.vedant.startupguard;

import com.vedant.startupguard.core.BaselineStore;
import com.vedant.startupguard.core.BeanInitTracker;
import com.vedant.startupguard.core.StartupAnalyzer;
import com.vedant.startupguard.core.StartupGuardListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(StartupGuardProperties.class)
@ConditionalOnProperty(name = "startup-guard.enabled", havingValue = "true") // Removed matchIfMissing so it defaults to false (safe for libs)
public class StartupGuardAutoConfiguration {

    @Bean
    public static BeanInitTracker beanInitTracker() {
        return new BeanInitTracker();
    }

    @Bean
    public BaselineStore baselineStore(StartupGuardProperties props) {
        return new BaselineStore(props.getBaselinePath());
    }

    @Bean
    public StartupAnalyzer startupAnalyzer(StartupGuardProperties props) {
        return new StartupAnalyzer(props.getThresholdPercent());
    }

    @Bean
    public StartupGuardListener startupGuardListener(BeanInitTracker tracker,
                                                     BaselineStore store,
                                                     StartupAnalyzer analyzer,
                                                     StartupGuardProperties props) { // Added props here
        return new StartupGuardListener(tracker, store, analyzer, props); // Pass props to the listener
    }
}