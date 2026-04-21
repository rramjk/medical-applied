package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Информация о здоровье пользователя")
public class UserHealthProfileDto {

    @Schema(description = "Идентификатор записи о здоровье пользователя")
    private UUID id;

    @Schema(description = "Идентификатор пользователя")
    private UUID userId;

    @Schema(description = "Вес пользователя в килограммах", example = "72.5")
    private Double weight;

    @Schema(description = "Список хронических заболеваний пользователя")
    private List<String> chronicConditions = new ArrayList<>();

    @Schema(description = "Список особенностей здоровья пользователя")
    private List<String> healthFeatures = new ArrayList<>();

    @Schema(description = "Список аллергий пользователя")
    private List<String> allergies = new ArrayList<>();

    @Schema(description = "Кем создана")
    private String createdBy;

    @Schema(description = "Дата создания")
    private LocalDateTime createdDate;

    @Schema(description = "Кем изменена")
    private String modifiedBy;

    @Schema(description = "Дата изменения")
    private LocalDateTime modifiedDate;
}