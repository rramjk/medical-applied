package tender.ma.medicalapplied.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseServiceException{
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(ErrorCode message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
