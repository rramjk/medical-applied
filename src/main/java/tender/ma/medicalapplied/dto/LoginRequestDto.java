package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Запрос на авторизацию")
public class LoginRequestDto {

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email is incorrect.")
    @Schema(description = "Почта пользователя")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    @Schema(description = "Пароль пользователя")
    private String password;
}
