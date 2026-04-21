package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Запрос на создание пользователя")
public class UserCreateRequestDto extends UserRequestDto {
    @Pattern(regexp = "[\\w.!%$]{5,15}",
            message = """
            Password size must be between 5 and 15
            Only [a-z, A-Z, 0-9, _, ., !, %, $] symbols.
            """)
    @NotBlank(message = "Password cannot be empty.")
    @Schema(description = "Пароль пользователя")
    private String password;
}
