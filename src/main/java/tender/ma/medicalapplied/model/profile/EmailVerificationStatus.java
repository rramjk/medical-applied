package tender.ma.medicalapplied.model.profile;

public enum EmailVerificationStatus {
    FAILED,
    UNVERIFIED, // заявка не создана
    CREATED, // при создании запроса
    PROCESSING, // при переведении в очередь
    SENT, // при отправке
    VERIFIED, // при подтверждении на почте
}
