package tender.ma.medicalapplied.service.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tender.ma.medicalapplied.config.ai.GeminiAiConfig;
import tender.ma.medicalapplied.dto.ai.AiChatRequest;
import tender.ma.medicalapplied.dto.ai.AiChatResponse;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.InternalServiceException;
import tender.ma.medicalapplied.service.client.AiClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientImpl implements AiClient {

    private final GeminiAiConfig config;
    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper;

    private static final String DEFAULT_SYSTEM_INSTRUCTION = """
            Ты медицинский ассистент. Не ставь диагноз. Отвечай осторожно и структурировано.
            """;
    private static final String USER_ROLE = "user";

    @Override
    public String sentMessage(String prompt) {
        log.debug("sentMessage: send prompt to AI model. Prompt size: {}", prompt.length());
        AiChatRequest request = getRequest(prompt);
        AiChatResponse response = sendAiRequest(request);
        validateResponse(response);

        return getMessageFromResponse(response);
    }

    private AiChatRequest getRequest(String prompt) {
        AiChatRequest.SystemInstruction systemInstruction = getStandartSystemInstruction();
        AiChatRequest.Content userContent = getContentForUserWithPrompt(prompt);

        return new AiChatRequest(
                systemInstruction,
                List.of(userContent)
        );
    }

    private void validateResponse(AiChatResponse response) {
        List<AiChatResponse.ResponsePart> parts = response.content().parts();
        if (parts.isEmpty() || parts.getFirst() == null) {
            throw new InternalServiceException(ErrorCode.AI_RETURN_EMPTY_MESSAGE);
        }
        AiChatResponse.ResponsePart part = parts.getFirst();
        if (part.text() == null || part.text().isEmpty()) {
            throw new InternalServiceException(ErrorCode.AI_RETURN_EMPTY_MESSAGE);
        }
    }

    private AiChatResponse sendAiRequest(AiChatRequest request) {
        log.debug("sendAiRequest: send to '{}' request to AI model '{}'", config.getUrl(), config.getModel());
        return aiRestClient.post()
                .uri(config.getUrl())
                .body(request)
                .exchange((_, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return getSuccessResponse(response);
                    } else {
                        throw new InternalServiceException("Ошибка при отправке запроса к модели. Ответ от сервера: \n" + readResponseBodyStr(response));
                    }
                });
    }

    private String getMessageFromResponse(AiChatResponse response) {
        return response
                .content()
                .parts()
                .getFirst()
                .text();
    }

    private AiChatResponse getSuccessResponse(ClientHttpResponse response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidate = root.path("candidates").get(0);
            return objectMapper.treeToValue(candidate, AiChatResponse.class);

        } catch (IOException e) {
            throw new InternalServiceException("Ошибка считывания JSON", e);
        }
    }

    private AiChatRequest.SystemInstruction getStandartSystemInstruction() {
        return new AiChatRequest.SystemInstruction(
                List.of(
                        new AiChatRequest.Part(DEFAULT_SYSTEM_INSTRUCTION)
                )
        );
    }

    private AiChatRequest.Content getContentForUserWithPrompt(String prompt) {
        return new AiChatRequest.Content(USER_ROLE,
                List.of(new AiChatRequest.Part(prompt))
        );
    }

    private String readResponseBodyStr(ClientHttpResponse response) {
        try {
            String responseStr = new String(
                    response.getBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            log.debug("response status: '{}'. body: {}", response.getStatusCode(), responseStr);

            return responseStr;
        } catch (IOException e) {
            throw new InternalServiceException("Ошибка чтения ответа Gemini API", e);
        }
    }
}
