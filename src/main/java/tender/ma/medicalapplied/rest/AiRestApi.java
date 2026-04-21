package tender.ma.medicalapplied.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface AiRestApi {
    String BASE_URL = "/api/ai";

    @GetMapping("/recommendation")
    String getRecommendation(
            @RequestParam String countryEn,
            @RequestParam String symptoms
    );
}
