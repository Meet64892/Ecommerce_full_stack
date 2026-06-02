package com.smartshop.order.config;

import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * ResilienceConfig - Programmatic Resilience4j retry defaults.
 *
 * <h2>Purpose</h2>
 * Transient failures (a brief Kafka broker hiccup, a momentary network blip) are
 * common in distributed systems. A bounded retry with backoff smooths over them
 * without hammering a struggling dependency.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Retry</b>: re-invoke a failed call up to {@code maxAttempts} times.</li>
 *   <li><b>Exponential backoff</b>: wait longer between each attempt (e.g. 500ms,
 *       1s, 2s) so we don't synchronize retries into a thundering herd.</li>
 *   <li>Most Resilience4j config can also live in application.yml; we show the
 *       programmatic registry here for clarity.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Supplies a named "kafkaPublish" retry the producer uses when sending events.
 *
 * @author SmartShop Team
 */
@Configuration
public class ResilienceConfig {

    /**
     * Builds a retry registry with an exponential-backoff policy.
     *
     * @return a {@link RetryRegistry} containing the "kafkaPublish" config
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                // Try the original call + up to 2 retries (3 total attempts).
                .maxAttempts(3)
                // Start at 500ms and double each time (exponential backoff).
                .intervalFunction(io.github.resilience4j.core.IntervalFunction
                        .ofExponentialBackoff(Duration.ofMillis(500), 2.0))
                .build();
        return RetryRegistry.of(config);
    }
}
