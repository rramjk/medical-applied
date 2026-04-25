package tender.ma.medicalapplied.service;

import tender.ma.medicalapplied.dto.ai.AnswerDto;

public interface AiService {

    String getRecommendation(String countryEn, String symptom);

    String ask(AnswerDto answerDto);
}
