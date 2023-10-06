package io.dexi.client;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Service
public class ServiceBClient {
    private final RestTemplate restTemplate;
    private final String serviceBBaseUrl; // The base URL of Service B
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;


    @Autowired
    public ServiceBClient(RestTemplate restTemplate, CircuitBreaker circuitBreaker, Bulkhead bulkhead) {
        this.restTemplate = restTemplate;
        this.serviceBBaseUrl = "http://localhost:8082"; // Replace with the actual base URL of Service B
        this.circuitBreaker = circuitBreaker;
        this.bulkhead = bulkhead;
    }

    public String getUser() {
        CheckedFunction0<String> decoratedSupplier = CircuitBreaker.decorateCheckedSupplier(
                circuitBreaker,
                () -> {
                    // Define the URL for the Service B endpoint you want to call
                    String serviceBUrl = serviceBBaseUrl + "/api/user";

                    // Make an HTTP GET request to Service B
                    String serviceBResponse = restTemplate.getForObject(serviceBUrl, String.class);

                    return serviceBResponse;
                }
        );
        Try<String> result = Try.of(decoratedSupplier).recover(throwable-> "EveryBody");
        return result.get();
    }


    public String fallbackHello(Exception e) {
        return "Every Body";
    }
}
