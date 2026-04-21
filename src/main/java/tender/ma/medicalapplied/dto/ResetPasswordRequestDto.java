package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Запрос на замену пароля")
public class ResetPasswordRequestDto {

    @NotBlank(message = "Password cannot be empty.")
    @Schema(description = "Старый пароль пользователя")
    private String oldPassword;

    @Pattern(regexp = "[\\w.!%$]{5,15}",
            message = """
            Password size must be between 5 and 15
            Only [a-z, A-Z, 0-9, _, ., !, %, $] symbols.
            """)
    @NotBlank(message = "Password cannot be empty.")
    @Schema(description = "Новый пароль пользователя")
    private String newPassword;
}
