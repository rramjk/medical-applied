package tender.ma.medicalapplied.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiChatResponse(
        ResponseContent content,
        String finishReason,
        Integer index
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResponseContent(
            List<ResponsePart> parts,
            String role
    ) {
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResponsePart(
            String text,
            String thoughtSignature
    ) {
    }
}