package tender.ma.medicalapplied.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import tender.ma.medicalapplied.dto.ai.AnswerDto;

@Validated
public interface AiService {

    String getRecommendation(@NotEmpty String countryEn, @NotEmpty String symptom);

    String ask(@Valid AnswerDto answerDto);
}
