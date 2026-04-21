package tender.ma.medicalapplied.repository.medical;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tender.ma.medicalapplied.dto.MedicalDto;
import tender.ma.medicalapplied.model.medical.Medical;
import tender.ma.medicalapplied.repository.redis.RedisRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRedisRepository {

    private static final String MEDICALS_FILTER_PREFIX = "medicals:filter:";
    private static final String MEDICAL_CATEGORIES_KEY = "medicals:categories";
    private static final String MEDICAL_NAMES_KEY = "medicals:names";
    private static final String MEDICAL_COUNTRIES_PREFIX = "medicals:countries:";

    private final RedisRepository repository;

    public Optional<List<MedicalDto>> getMedicalsByFilter(String countryEn, String category, String name) {
        String key = buildMedicalsFilterKey(countryEn, category, name);
        log.debug("getMedicalsByFilter: key={}", key);
        return repository.getList(key, MedicalDto.class);
    }

    public List<MedicalDto> saveMedicalsByFilter(String countryEn, String category, String name, List<MedicalDto> medicals) {
        String key = buildMedicalsFilterKey(countryEn, category, name);
        log.debug("saveMedicalsByFilter: key={}, size={}", key, medicals.size());
        repository.save(key, medicals);
        return medicals;
    }

    public Optional<List<String>> getMedicalCategories() {
        log.debug("getMedicalCategories: key={}", MEDICAL_CATEGORIES_KEY);
        return repository.getList(MEDICAL_CATEGORIES_KEY, String.class);
    }

    public Optional<List<String>> getMedicalNames() {
        log.debug("getMedicalNames: key={}", MEDICAL_NAMES_KEY);
        return repository.getList(MEDICAL_NAMES_KEY, String.class);
    }

    public Optional<List<String>> getMedicalCountries(boolean translateCountryName) {
        String key = buildMedicalCountriesKey(translateCountryName);
        log.debug("getMedicalCountries: key={}", key);
        return repository.getList(key, String.class);
    }

    public List<String> saveMedicalCategories(List<String> medicalCategories) {
        log.debug("saveMedicalCategories: key={}, size={}", MEDICAL_CATEGORIES_KEY, medicalCategories.size());
        repository.save(MEDICAL_CATEGORIES_KEY, medicalCategories);
        return medicalCategories;
    }

    public List<String> saveMedicalNames(List<String> medicalNames) {
        log.debug("saveMedicalNames: key={}, size={}", MEDICAL_NAMES_KEY, medicalNames.size());
        repository.save(MEDICAL_NAMES_KEY, medicalNames);
        return medicalNames;
    }

    public List<String> saveMedicalCountries(boolean translateCountryName, List<String> medicalCountries) {
        String key = buildMedicalCountriesKey(translateCountryName);
        log.debug("saveMedicalCountries: key={}, size={}", key, medicalCountries.size());
        repository.save(key, medicalCountries);
        return medicalCountries;
    }

    private String buildMedicalsFilterKey(String countryEn, String category, String name) {
        return MEDICALS_FILTER_PREFIX
                + "countryEn=" + normalize(countryEn)
                + ":category=" + normalize(category)
                + ":name=" + normalize(name);
    }

    private String buildMedicalCountriesKey(boolean translateCountryName) {
        return MEDICAL_COUNTRIES_PREFIX + (translateCountryName ? "en" : "ru");
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "_";
        }
        return URLEncoder.encode(value.trim(), StandardCharsets.UTF_8);
    }
}
