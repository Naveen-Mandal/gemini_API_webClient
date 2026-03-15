package com.naveenmandal.project1.model;


import java.util.List;

public class GeminiRequest {

    private List<Content> contents;

    // Constructor
    public GeminiRequest(String userMessage) {
        Part part = new Part(userMessage);
        Content content = new Content(List.of(part));
        this.contents = List.of(content);
    }

    public List<Content> getContents() { return contents; }

    // --- Inner Classes ---

    public static class Content {
        private List<Part> parts;
        public Content(List<Part> parts) { this.parts = parts; }
        public List<Part> getParts() { return parts; }
    }

    public static class Part {
        private String text;
        public Part(String text) { this.text = text; }
        public String getText() { return text; }
    }
}
