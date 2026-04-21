package tender.ma.medicalapplied.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // AUTH
    AUTHORIZATION_FAILED("Некорректные данные для пользователя"),
    USER_FOR_AUTHORIZE_NOT_FOUND("Не найден пользователь с email '%s'"),
    USER_EMPTY_IN_AUTHORIZE_CONTEXT("Пользователь не содержится в контексте авторизации"),
    USER_WITH_THIS_EMAIL_EXISTS("Пользователь с email '%s' уже зарегистрирован"),
    USER_NOT_FOUND("Не найден пользователь с id '%s'"),
    MEDICAL_NOT_FOUND("Не найден препарат с id '%s'"),
    USER_HEALTH_PROFILE_NOT_FOUND("Профиль здоровья с id '%s' не найден"),
    USER_HEALTH_PROFILE_AND_USER_NOT_LINKED("Профиль здоровья с id '%s' не принадлежит пользователю с id '%s'"),
    USER_HEALTH_PROFILE_NOT_FOUND_FOR_USER("Профиль здоровья для пользователя с id '%s' не найден"),
    REQUEST_FOR_VERIFICATION_ALREADY_EXISTS("Запрос на верификацию пользователя по почте уже отправлен"),
    REQUEST_FOR_VERIFICATION_NOT_FOUND("Не найден запрос на верификацию или он его длительность истекла для пользователя с id '%s'"),
    REQUEST_FOR_VERIFICATION_INCORRECT("Некорректный токен верификации для пользователя с id '%s'"),
    AI_RETURN_EMPTY_MESSAGE("ИИ-ассистент вернул пустое сообщение"),
    AI_UNLOCKED_ONLY_FOR_VERIFIED_USER("ИИ-ассистент доступ только для верифицированных пользователей")
    ;

    private final String message;

    public String getErrorMessage(Object... args) {
        return String.format(message, args);
    }
}
