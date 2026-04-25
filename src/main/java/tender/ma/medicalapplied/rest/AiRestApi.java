package tender.ma.medicalapplied.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tender.ma.medicalapplied.dto.ai.AnswerDto;

public interface AiRestApi {
    String BASE_URL = "/api/ai";

    @GetMapping("/recommendation")
    String getRecommendation(
            @RequestParam String countryEn,
            @RequestParam String symptoms
    );

    @PostMapping("/answer")
    String ask(@RequestBody AnswerDto answerDto);
}
