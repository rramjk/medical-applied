package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tender.ma.medicalapplied.model.user.GenderTypes;
import tender.ma.medicalapplied.model.user.RoleTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Пользователь")
public class UserDto{

    @Schema(description = "Идентификатор пользователя")
    private UUID id;

    @Schema(description = "Имя пользователя")
    private String firstName;

    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @Schema(description = "Дата рождения пользователя")
    private LocalDate birthDate;

    @Schema(description = "Пол пользователя")
    private GenderTypes gender;

    @Schema(description = "Идентификатор роли")
    private UUID roleId;

    @Schema(description = "Название роли")
    private RoleTypes roleName;

    @Schema(description = "Описание роли")
    private String roleDescription;

    @Schema(description = "Почта пользователя")
    private String email;

    @Schema(description = "Подтверждение почты")
    private boolean emailVerified;

    @Schema(description = "Согласие на пользовательское соглашение")
    private boolean userConsent;

    @Schema(description = "Согласие на политику конфиденциальности")
    private boolean privacyConsent;

    @Schema(description = "Кем создана")
    private String createdBy;

    @Schema(description = "Дата создания")
    private LocalDateTime createdDate;

    @Schema(description = "Кем изменена")
    private String modifiedBy;

    @Schema(description = "Дата изменения")
    private LocalDateTime modifiedDate;
}
