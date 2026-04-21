package tender.ma.medicalapplied.service.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tender.ma.medicalapplied.config.ai.DeepseekAiConfig;
import tender.ma.medicalapplied.dto.ai.AiChatRequest;
import tender.ma.medicalapplied.dto.ai.AiChatResponse;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.InternalServiceException;
import tender.ma.medicalapplied.service.client.AiClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientImpl implements AiClient {

    private final RestClient restClient;
    private final DeepseekAiConfig config;
    private static final String CHAT_URI = "/chat/completions";

    @Override
    public String getRecommendation(String prompt) {
        log.debug("getRecommendation: send prompt to AI model. Prompt size: {}", prompt.length());
        AiChatRequest request = getRequest(prompt);
        AiChatResponse response = sendDeepseekRequest(request);
        validateResponse(response);

        return getMessageFromResponse(response);
    }

    private AiChatRequest getRequest(String prompt) {
        return new AiChatRequest(
                config.getModel(),
                List.of(
                        new AiChatRequest.Message(
                                "system",
                                "Ты медицинский ассистент. Не ставь диагноз. Отвечай осторожно и структурировано."
                        ),
                        new AiChatRequest.Message(
                                "user",
                                prompt
                        )
                ),
                false
        );
    }

    private void validateResponse(AiChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new InternalServiceException(ErrorCode.AI_RETURN_EMPTY_MESSAGE);
        }

        AiChatResponse.Message message = response.choices().getFirst().message();
        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new InternalServiceException(ErrorCode.AI_RETURN_EMPTY_MESSAGE);
        }
    }

    private AiChatResponse sendDeepseekRequest(AiChatRequest request) {
        log.debug("sendDeepseekRequest: send request to AI model");
        return restClient.post()
                .uri(CHAT_URI)
                .body(request)
                .retrieve()
                .body(AiChatResponse.class);
    }

    private String getMessageFromResponse(AiChatResponse response) {
        return response.choices().getFirst().message().content();
    }
}
