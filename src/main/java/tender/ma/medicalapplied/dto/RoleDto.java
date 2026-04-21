package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tender.ma.medicalapplied.model.user.RoleTypes;

import java.util.UUID;

@Data
@Schema(name = "Роль пользователя")
public class RoleDto {

    @Schema(description = "Идентификатор роли")
    private UUID id;

    @Schema(description = "Тип роли")
    private RoleTypes name;

    @Schema(description = "Описание роли")
    private String description;
}
