package tender.ma.medicalapplied;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import tender.ma.medicalapplied.config.ai.GeminiAiConfig;


@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({GeminiAiConfig.class})
public class MedicalAppliedApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedicalAppliedApplication.class, args);
	}

}
