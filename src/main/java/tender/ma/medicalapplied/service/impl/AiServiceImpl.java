package tender.ma.medicalapplied.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import tender.ma.medicalapplied.config.security.utils.AuthUserService;
import tender.ma.medicalapplied.dto.MedicalDto;
import tender.ma.medicalapplied.dto.UserHealthProfileDto;
import tender.ma.medicalapplied.dto.ai.AnswerDto;
import tender.ma.medicalapplied.exceptions.BadRequestException;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.NotFoundException;
import tender.ma.medicalapplied.model.user.User;
import tender.ma.medicalapplied.service.AiService;
import tender.ma.medicalapplied.service.MedicalService;
import tender.ma.medicalapplied.service.UserHealthProfileService;
import tender.ma.medicalapplied.service.client.AiClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final AuthUserService authService;
    private final UserHealthProfileService userHealthProfileService;
    private final MedicalService medicalService;
    private final AiClient client;
    private final ResourceLoader resourceLoader;

    private static final int MAX_MEDICALS_IN_PROMPT_SIZE = 10;
    private static final String RECOMMENDATION_PROMPT_FILE_NAME = "recommendations-prompt.txt";
    private static final String ANSWER_PROMPT_FILE_NAME = "answers-prompt.txt";


    @Override
    public String getRecommendation(String countryEn, String symptom) {
        log.info("getRecommendation: country={}, symptom={}", countryEn, symptom);

        User user = authService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_EMPTY_IN_AUTHORIZE_CONTEXT));
        UserHealthProfileDto profile =
                userHealthProfileService.getUserHealthProfileByUserId(user.getId());

        validatePermissionForUseAiAgent(user, profile);

        String prompt = buildPromptForRecommendation(countryEn, symptom, user, profile);
        return client.sentMessage(prompt);
    }

    @Override
    public String ask(AnswerDto answerDto) {
        log.info("ask: answer size: {}", answerDto.getText().length());

        User user = authService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_EMPTY_IN_AUTHORIZE_CONTEXT));
        UserHealthProfileDto profile =
                userHealthProfileService.getUserHealthProfileByUserId(user.getId());

        validatePermissionForUseAiAgent(user, profile);

        String prompt = buildPromptForAnswer(answerDto, user, profile);
        return client.sentMessage(prompt);
    }

    private void validatePermissionForUseAiAgent(User user, UserHealthProfileDto profile) {
        if (!user.isEmailVerified() || !user.isUserConsent() || !user.isPrivacyConsent() || profile == null) {
            throw new BadRequestException(ErrorCode.AI_UNLOCKED_ONLY_FOR_VERIFIED_USER);
        }
    }

    private String buildPromptForRecommendation(String country, String symptom, User user, UserHealthProfileDto profile) {
        log.debug("buildPromptForRecommendation: build prompt");
        return getRecommendationPromptFormat().formatted(
                normalize(country),
                normalize(symptom),
                normalize(user.getGender()),
                normalize(user.getBirthDate()),
                normalize(profile.getWeight()),
                formatList(profile.getChronicConditions()),
                formatList(profile.getHealthFeatures()),
                formatList(profile.getAllergies()),
                formatMedicalsList(medicalService.getMedicals(country, symptom, null))
        );
    }

    private String buildPromptForAnswer(AnswerDto answer, User user, UserHealthProfileDto profile) {
        log.debug("buildPromptForAnswer: build prompt");
        return getAnswerPromptFormat().formatted(
                normalize(user.getGender()),
                normalize(user.getBirthDate()),
                normalize(profile.getWeight()),
                formatList(profile.getChronicConditions()),
                formatList(profile.getHealthFeatures()),
                formatList(profile.getAllergies()),
                answer.getText()
        );
    }

    private String getRecommendationPromptFormat() {
        Resource page = resourceLoader.getResource("classpath:prompts/" + RECOMMENDATION_PROMPT_FILE_NAME);
        try (InputStream inputStream = page.getInputStream()) {
            return getStringPageFromFormatFileInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAnswerPromptFormat() {
        Resource page = resourceLoader.getResource("classpath:prompts/" + ANSWER_PROMPT_FILE_NAME);
        try (InputStream inputStream = page.getInputStream()) {
            return getStringPageFromFormatFileInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String normalize(Object value) {
        return value == null ? "не указано" : String.valueOf(value).trim();
    }

    private String formatList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "не указано";
        }

        return values.stream()
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(", "));
    }

    private String formatMedicalsList(List<MedicalDto> medicals) {
        if (medicals == null || medicals.isEmpty()) {
            return "не указано";
        }

        return medicals.stream()
                .filter(Objects::nonNull)
                .limit(MAX_MEDICALS_IN_PROMPT_SIZE)
                .map(this::medicalToString)
                .collect(Collectors.joining(";\n"));
    }

    private String medicalToString(MedicalDto medical) {
        return """
                Название: %s; Категория: %s; Действующее вещество: %s; Показания: %s; Противопоказания: %s
                """.formatted(
                normalize(medical.getName()),
                normalize(medical.getType()),
                normalize(medical.getActiveIngredient()),
                normalize(medical.getIndications()),
                normalize(medical.getContraindications())
        ).trim();
    }

    private String getStringPageFromFormatFileInputStream(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
