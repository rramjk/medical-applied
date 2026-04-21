package tender.ma.medicalapplied.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tender.ma.medicalapplied.exceptions.BaseServiceException;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.InternalServiceException;

import java.util.function.Function;

/**
 * Класс для работы с исключениями
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    /**
     * Обернуть внутреннее исключение во внешнее, если исключение внутреннее
     *
     * @param errorCode код ошибки
     */
    public static Function<Throwable, Throwable> extExceptionMapper(ErrorCode errorCode) {
        return extExceptionMapper(errorCode.getMessage());
    }

    public static Function<Throwable, Throwable> extExceptionMapper(String errorMessage) {
        return throwable -> throwable instanceof BaseServiceException
                ? throwable
                : new InternalServiceException(String.format("%s. %s", errorMessage, getErrorMessage(throwable)));
    }

    private static String getErrorMessage(Throwable throwable) {
        return throwable.getCause() != null
                ? throwable.getCause().getMessage()
                : throwable.getMessage();
    }
}
