package tender.ma.medicalapplied.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseServiceException extends RuntimeException {
    private final HttpStatus status;

    public BaseServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BaseServiceException(ErrorCode message, HttpStatus status) {
        super(message.getMessage());
        this.status = status;
    }

    public BaseServiceException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
