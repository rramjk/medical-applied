package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import tender.ma.medicalapplied.model.user.GenderTypes;

import java.time.LocalDate;

@Data
@Schema(description = "Запрос на создание/обновление пользователя")
public class UserRequestDto {

    @NotBlank(message = "First name cannot be empty.")
    @Size(min = 2, max = 12, message = "First name size must be between 2 and 12 characters.")
    @Pattern(regexp = "[\\p{L}-]{2,12}", message = "First name is incorrect.")
    @Schema(description = "Имя пользователя")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty.")
    @Size(min = 2, max = 12, message = "Last name size must be between 2 and 12 characters.")
    @Pattern(regexp = "[\\p{L}-]{2,12}", message = "Last name is incorrect.")
    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @NotNull(message = "Birth date cannot be empty.")
    @Schema(description = "Дата рождения пользователя")
    private LocalDate birthDate;

    @NotNull(message = "Gender must be selected.")
    @Schema(description = "Пол пользователя")
    private GenderTypes gender;

    @Pattern(regexp = "[\\w.]{1,25}@gmail.com",
            message = """
                Email is incorrect. Only gmail.com is allowed.
            """)
    @NotBlank(message = "Email cannot be empty.")
    @Schema(description = "Почта пользователя")
    private String email;

    @NotNull
    @Schema(description = "Согласие на пользовательское соглашение")
    private Boolean userConsent = false;

    @NotNull
    @Schema(description = "Согласие на политику конфиденциальности")
    private Boolean privacyConsent = false;
}
