package com.naveenmandal.project1.controller;


import com.naveenmandal.project1.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    // POST endpoint: /api/gemini/ask
    // Body: { "message": "What is Java?" }
    @PostMapping("/ask")
    public String ask(@RequestBody java.util.Map<String, String> body) {
        System.out.println(">>> Received request from Postman at: " + java.time.LocalTime.now());

        String message = body.get("message");
        return geminiService.askGemini(message);
//        String message = body.get("message");
//        return geminiService.askGemini(message);

    }
}
