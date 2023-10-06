package io.dexi.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;
import static java.time.Duration.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Configures the failure rate threshold in percentage
                .slowCallRateThreshold(100) // Configures a threshold in percentage
                .slowCallDurationThreshold(ofMillis(5000))//Configures the duration threshold above which calls are considered as slow
                .permittedNumberOfCallsInHalfOpenState(5) //Configures the number of permitted calls when the CircuitBreaker is half open
                .slidingWindowType(COUNT_BASED) //Configures the minimum number of calls which are required (per sliding window period)
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // If set to true it means that the CircuitBreaker will automatically transition from open to half-open state
                .waitDurationInOpenState(Duration.ofMillis(5000)) //The time that the CircuitBreaker should wait before transitioning from open to half-open.
                .recordExceptions(Exception.class) //A list of exceptions that are recorded as a failure and thus increase the failure rate
                //.ignoreException() A list of exceptions that are ignored and neither count as a failure nor success.
                //.recordException(e -> INTERNAL_SERVER_ERROR.equals(e.getMessage()))
                .build();
        return CircuitBreaker.of("serviceBCircuitBreaker", config);
    }

    @Bean
    public Bulkhead bulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(10) // Adjust concurrency limit as needed
                .maxWaitDuration(ofMillis(100)) // Adjust wait duration as needed
                .build();

        return Bulkhead.of("serviceBBulkhead", config);
    }
}
