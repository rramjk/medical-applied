package tender.ma.medicalapplied.exceptions;

import org.springframework.http.HttpStatus;

public class UserAuthorizationException extends BaseServiceException {
    public UserAuthorizationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UserAuthorizationException(ErrorCode message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
