package tender.ma.medicalapplied.dto.ai;

import java.util.List;

public record AiChatRequest(SystemInstruction systemInstruction, List<Content> contents) {
    public record SystemInstruction(List<Part> parts) {}
    public record Content(String role, List<Part> parts) {}
    public record Part(String text) {}
}
