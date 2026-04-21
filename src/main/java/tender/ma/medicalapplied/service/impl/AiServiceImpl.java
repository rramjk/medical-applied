package tender.ma.medicalapplied.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tender.ma.medicalapplied.config.security.utils.AuthUserService;
import tender.ma.medicalapplied.dto.MedicalDto;
import tender.ma.medicalapplied.dto.UserHealthProfileDto;
import tender.ma.medicalapplied.exceptions.BadRequestException;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.NotFoundException;
import tender.ma.medicalapplied.model.user.User;
import tender.ma.medicalapplied.service.AiService;
import tender.ma.medicalapplied.service.MedicalService;
import tender.ma.medicalapplied.service.UserHealthProfileService;
import tender.ma.medicalapplied.service.client.AiClient;

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

    private static final int MAX_MEDICALS_IN_PROMPT_SIZE = 10;

    @Override
    public String getRecommendation(String countryEn, String symptom) {
        log.info("getRecommendation: country={}, symptom={}", countryEn, symptom);

        User user = authService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_EMPTY_IN_AUTHORIZE_CONTEXT));
        UserHealthProfileDto profile =
                userHealthProfileService.getUserHealthProfileByUserId(user.getId());

        validatePermissionForUseAiAgent(user, profile);

        String prompt = buildPrompt(countryEn, symptom, user, profile);
        return client.getRecommendation(prompt);
    }

    private void validatePermissionForUseAiAgent(User user, UserHealthProfileDto profile) {
        if (!user.isEmailVerified() || !user.isUserConsent() || !user.isPrivacyConsent() || profile == null) {
            throw new BadRequestException(ErrorCode.AI_UNLOCKED_ONLY_FOR_VERIFIED_USER);
        }
    }

    private String buildPrompt(String country, String symptom, User user, UserHealthProfileDto profile) {
        log.debug("buildPrompt: build prompt");
        return getPromptFormat().formatted(
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

    private String getPromptFormat() {
        return """
                Привет! Ты профессиональный врач-терапевт.
                На основании входных данных определи:
                1. Какие группы препаратов могут подойти пациенту.
                2. Какие конкретные препараты можно рассмотреть.
                3. Какие есть противопоказания и ограничения.
                4. На что обратить внимание перед применением.
                5. В каких случаях нужно срочно обратиться к врачу.
                Важно:
                - Не ставь окончательный диагноз.
                - Учитывай ограничения по аллергиям, хроническим заболеваниям и особенностям здоровья.
                - Если данных недостаточно, прямо укажи это.
                - Ответ дай на русском языке.
                - Ответ структурируй по пунктам.
                - Если список переданных препаратов, среди которых надо провести анализ, пуст то строго дай ответ: Извините, не найдены подходящие препараты
                Данные:
                Страна: %s
                Симптом (От чего): %s
                Пол пациента: %s
                Дата рождения пациента: %s
                Вес: %s
                Хронические заболевания: %s
                Особенности здоровья: %s
                Аллергии: %s
                Список препаратов, среди которых надо провести анализ:
                %s
                """;
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
}
