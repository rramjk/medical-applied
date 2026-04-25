package tender.ma.medicalapplied.config.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class GeminiAiWebClientConfig {
    private final GeminiAiConfig config;
    private static final String GOOG_API_KEY_HEADER = "x-goog-api-key";
    private static final int CONNECT_TIMEOUT = 20;
    private static final int READ_TIMEOUT = 180;

    @Bean
    public RestClient aiRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout((int) Duration.ofSeconds(READ_TIMEOUT).toMillis());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader(GOOG_API_KEY_HEADER, config.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
