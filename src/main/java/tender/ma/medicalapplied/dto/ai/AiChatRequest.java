package tender.ma.medicalapplied.dto.ai;

import java.util.List;

public record AiChatRequest(String model, List<Message> messages, Boolean stream) {
    public record Message(String role, String content) {}
}
