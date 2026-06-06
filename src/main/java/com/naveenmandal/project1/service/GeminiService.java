package com.naveenmandal.project1.service;

import com.naveenmandal.project1.model.GeminiRequest;
import com.naveenmandal.project1.model.GeminiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Autowired
    private WebClient webClient;

    public Mono<GeminiResponse> callGeminiApi(GeminiRequest request) {
        String endpoint = "/v1beta/models/gemini-pro:generateContent";

        logger.info("Initiating outbound reactive stream request to Gemini API endpoint.");

        return webClient.post()
                .uri(endpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                // Exponential Backoff Retry Strategy for handling rate limits (HTTP 429) or timeouts
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> isRetryableException(throwable))
                        .doBeforeRetry(retrySignal -> logger.warn(
                                "Gemini API gateway threshold reached or timed out. Retrying execution stream. Attempt #{} of 3. Reason: {}",
                                retrySignal.totalRetries() + 1,
                                retrySignal.failure().getMessage()
                        ))
                )
                // Traceability and logging validation footprints
                .doOnSuccess(response -> logger.info("Successfully fetched and parsed nested payload structure from Gemini API."))
                .doOnError(error -> logger.error("Fatal failure downstream. Unable to resolve API call stream after retries."))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Root cause identified - HTTP Client Defect: Status Code: {}, Response Metadata: {}", 
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("Downstream Gemini API error encountered: " + ex.getStatusText()));
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Root cause identified - Unexpected System Exception during data transit: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Internal stream execution defect: " + ex.getMessage()));
                });
    }

    private boolean isRetryableException(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException webEx = (WebClientResponseException) throwable;
            // Retry on HTTP 429 Too Many Requests (Rate Limits) or Server Errors (5xx)
            return webEx.getStatusCode().value() == 429 || webEx.getStatusCode().is5xxServerError();
        }
        return throwable instanceof java.io.IOException || throwable instanceof java.util.concurrent.TimeoutException;
    }
}