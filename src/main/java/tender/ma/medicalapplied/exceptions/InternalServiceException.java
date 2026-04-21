package tender.ma.medicalapplied.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServiceException extends BaseServiceException{
    public InternalServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServiceException(ErrorCode message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
