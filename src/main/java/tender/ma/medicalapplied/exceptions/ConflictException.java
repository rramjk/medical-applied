package tender.ma.medicalapplied.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseServiceException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(ErrorCode message) {
        super(message, HttpStatus.CONFLICT);
    }
}
