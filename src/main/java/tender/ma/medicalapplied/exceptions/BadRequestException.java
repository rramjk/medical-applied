package tender.ma.medicalapplied.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseServiceException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(ErrorCode message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
