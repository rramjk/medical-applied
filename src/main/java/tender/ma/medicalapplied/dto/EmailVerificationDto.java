package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tender.ma.medicalapplied.model.profile.EmailVerificationStatus;

@Data
@Schema(description = "Запроса на верификацию")
public class EmailVerificationDto {

    @Schema(description = "Статус запроса")
    private EmailVerificationStatus status;
}
