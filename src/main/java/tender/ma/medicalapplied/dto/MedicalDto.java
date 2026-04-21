package tender.ma.medicalapplied.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(name = "Медицинский препарат")
public class MedicalDto {
    @Schema(
            description = "Уникальный идентификатор медицинского препарата",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Название страны на английском языке",
            example = "Germany"
    )
    private String countryEn;

    @Schema(
            description = "Название страны на русском языке",
            example = "Германия"
    )
    private String countryRu;

    @Schema(
            description = "Тип или категория медицинского препарата",
            example = "Обезболивающее"
    )
    private String type;

    @Schema(
            description = "Название медицинского препарата",
            example = "Нурофен"
    )
    private String name;

    @Schema(
            description = "Действующее вещество препарата",
            example = "Ибупрофен"
    )
    private String activeIngredient;

    @Schema(
            description = "Общее описание препарата, его свойства и назначение",
            example = "Нестероидный противовоспалительный препарат с обезболивающим и жаропонижающим действием"
    )
    private String description;

    @Schema(
            description = "Показания к применению препарата",
            example = "Головная боль, зубная боль, мышечная боль, повышенная температура"
    )
    private String indications;

    @Schema(
            description = "Противопоказания к применению препарата",
            example = "Язвенная болезнь желудка, аллергия на ибупрофен, тяжелая почечная недостаточность"
    )
    private String contraindications;

    @Schema(
            description = "Рекомендации по дозировке и способу применения",
            example = "По 1 таблетке 2-3 раза в день после еды"
    )
    private String dosing;

    @Schema(
            description = "Признак того, что препарат допустим к применению при заболеваниях почек",
            example = "true"
    )
    private Boolean kidneyFriendly;

    @Schema(
            description = "Признак того, что препарат допустим к применению при беременности",
            example = "false"
    )
    private Boolean pregnantFriendly;

    @Schema(
            description = "Признак того, что препарат допустим к применению в период грудного вскармливания",
            example = "false"
    )
    private Boolean breastfedFriendly;

    @Schema(
            description = "Признак того, что препарат допустим к применению при заболеваниях печени",
            example = "true"
    )
    private Boolean liverFriendly;

    @Schema(
            description = "Признак того, что препарат допустим к применению детям",
            example = "true"
    )
    private Boolean childFriendly;

    @Schema(
            description = "Признак того, что препарат щадящий для желудка",
            example = "false"
    )
    private Boolean stomachFriendly;
}
