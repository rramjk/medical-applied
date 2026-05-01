package tender.ma.medicalapplied.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerDto {
    @NotEmpty(message = "Answer cannot be empty!")
    @Size(max = 100, message = "Answer too long!")
    private String text;
}
