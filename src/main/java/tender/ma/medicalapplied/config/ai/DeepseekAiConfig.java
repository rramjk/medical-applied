package tender.ma.medicalapplied.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.ai.deepseek")
public class DeepseekAiConfig {
    private String baseUrl;
    private String apiKey;
    private String model;
}
