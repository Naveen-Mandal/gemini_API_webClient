package com.naveenmandal.project1.model;

import java.util.List;

public class GeminiResponse {

    private List<Candidate> candidates;

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    // Helper method to directly get the text
    public String getResponseText() {
        return candidates.get(0)
                .getContent()
                .getParts().get(0)
                .getText();
    }

    // --- Inner Classes (mirrors Gemini's JSON structure) ---

    public static class Candidate {
        private Content content;
        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }
    }

    public static class Content {
        private List<Part> parts;
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    public static class Part {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
