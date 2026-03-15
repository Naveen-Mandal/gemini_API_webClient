package com.naveenmandal.project1.service;


import com.naveenmandal.project1.model.GeminiRequest;
import com.naveenmandal.project1.model.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GeminiService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000; // 5 seconds between retries

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String askGemini(String userMessage) {
        GeminiRequest request = new GeminiRequest(userMessage);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                GeminiResponse response = webClient.post()
                        .uri(apiUrl + "?key=" + apiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(GeminiResponse.class)
                        .block();

                if (response != null && response.getCandidates() != null) {
                    return response.getResponseText();
                }
                return "No response from Gemini";

            } catch (WebClientResponseException.TooManyRequests e) {
                // 429 error — rate limited, retry after delay
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt); // exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return "❌ Request interrupted.";
                    }
                } else {
                    return "⚠️ Rate limit exceeded after " + MAX_RETRIES + " retries. "
                            + "The free tier allows ~15 requests/minute. Please wait and try again.";
                }

            } catch (WebClientResponseException e) {
                return "❌ API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();

            } catch (Exception e) {
                return "❌ Something went wrong: " + e.getMessage();
            }
        }
        return "❌ Failed after " + MAX_RETRIES + " attempts.";
    }
}