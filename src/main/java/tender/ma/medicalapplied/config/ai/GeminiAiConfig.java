package tender.ma.medicalapplied.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.ai.gemini")
public class GeminiAiConfig {
    private String apiKey;
    private String baseUrl;
    private String url;
    private String action;
    private String model;
}
