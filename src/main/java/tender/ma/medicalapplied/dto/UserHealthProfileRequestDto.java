package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "Запрос на создание/обновление информации о здоровье пользователя")
public class UserHealthProfileRequestDto {

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0.")
    @Schema(description = "Вес пользователя в килограммах", example = "72.5")
    private Double weight;

    @Size(max = 50, message = "Chronic conditions list size must be less than or equal to 50.")
    @Schema(description = "Список хронических заболеваний пользователя")
    private List<String> chronicConditions = new ArrayList<>();

    @Size(max = 50, message = "Health features list size must be less than or equal to 50.")
    @Schema(description = "Список особенностей здоровья пользователя")
    private List<String> healthFeatures = new ArrayList<>();

    @Size(max = 50, message = "Allergies list size must be less than or equal to 50.")
    @Schema(description = "Список аллергий пользователя")
    private List<String> allergies = new ArrayList<>();
}