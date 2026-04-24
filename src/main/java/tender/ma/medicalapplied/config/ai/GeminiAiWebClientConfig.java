package tender.ma.medicalapplied.config.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class GeminiAiWebClientConfig {
    private final GeminiAiConfig config;
    private static final String GOOG_API_KEY_HEADER = "x-goog-api-key";

    @Bean
    public RestClient aiRestClient() {
        return RestClient.builder()
                .defaultHeader(GOOG_API_KEY_HEADER, config.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
