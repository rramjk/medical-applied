package tender.ma.medicalapplied.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tender.ma.medicalapplied.config.security.utils.AuthUserService;
import tender.ma.medicalapplied.dto.MedicalDto;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.NotFoundException;
import tender.ma.medicalapplied.model.mapping.MedicalMapper;
import tender.ma.medicalapplied.model.mapping.MedicalViewHistoryMapper;
import tender.ma.medicalapplied.model.medical.Medical;
import tender.ma.medicalapplied.model.user.User;
import tender.ma.medicalapplied.repository.MedicalViewHistoryRepository;
import tender.ma.medicalapplied.repository.medical.MedicalRedisRepository;
import tender.ma.medicalapplied.repository.medical.MedicalRepository;
import tender.ma.medicalapplied.repository.medical.MedicalSpecification;
import tender.ma.medicalapplied.service.MedicalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalServiceImpl implements MedicalService {
    private final MedicalRepository repository;
    private final MedicalMapper mapper;
    private final MedicalViewHistoryRepository medicalViewHistoryRepository;
    private final MedicalViewHistoryMapper mvhMapper;
    private final AuthUserService authUserService;
    private final MedicalRedisRepository redisRepository;

    @Override
    public List<MedicalDto> getMedicals(String countryEn, String category, String name) {
        log.info("getMedicals: getting medical by filter");

        Optional<List<MedicalDto>> medicalsFromRedis =
                redisRepository.getMedicalsByFilter(countryEn, category, name);
        if (medicalsFromRedis.isPresent()) {
            log.debug("getMedicals: cache hit");
            return medicalsFromRedis.get();
        } else {
            log.debug("getMedicals: cache miss, loading from db");
            List<MedicalDto> medicalsDtoList = repository.findAll(MedicalSpecification.byFilters(countryEn, category, name))
                    .stream()
                    .map(mapper::toDto)
                    .toList();
            redisRepository.saveMedicalsByFilter(countryEn, category, name, medicalsDtoList);

            return medicalsDtoList;
        }
    }

    @Override
    public MedicalDto getMedicalById(UUID id) {
        log.info("getMedicalById: getting medical by id {}", id);
        Medical medical = getByIdOrThrowExc(id);
        try {
            saveMedicalToUserViewHistory(medical);
        } catch (Exception e) {
            log.warn("getMedicalById: saveMedicalToUserViewHistory throws exception ", e);
        }
        return mapper.toDto(medical);
    }

    @Override
    public List<String> getMedicalCategories() {
        log.info("getMedicalCategories: getting medical categories");

        Optional<List<String>> categoriesFromRedis = redisRepository.getMedicalCategories();
        if (categoriesFromRedis.isPresent()) {
            log.debug("getMedicalCategories: cache hit");
            return categoriesFromRedis.get();
        }

        log.debug("getMedicalCategories: cache miss, loading from db");
        List<String> categoriesFromDb = repository.getAllTypes();
        redisRepository.saveMedicalCategories(categoriesFromDb);

        return categoriesFromDb;
    }

    @Override
    public List<String> getMedicalCountries(boolean translateCountryName) {
        log.info("getMedicalCountries: getting medical countries with translateCountryName {}", translateCountryName);

        Optional<List<String>> countriesFromRedis =
                redisRepository.getMedicalCountries(translateCountryName);

        if (countriesFromRedis.isPresent()) {
            log.debug("getMedicalCountries: cache hit, translateCountryName={}", translateCountryName);
            return countriesFromRedis.get();
        }

        log.debug("getMedicalCountries: cache miss, loading from db, translateCountryName={}", translateCountryName);
        List<String> countriesFromDb = translateCountryName
                ? repository.getAllCountriesEn()
                : repository.getAllCountriesRu();
        redisRepository.saveMedicalCountries(translateCountryName, countriesFromDb);

        return countriesFromDb;
    }

    @Override
    public List<String> getMedicalNames() {
        log.info("getMedicalNames: getting medical names");

        Optional<List<String>> namesFromRedis = redisRepository.getMedicalNames();
        if (namesFromRedis.isPresent()) {
            log.debug("getMedicalNames: cache hit");
            return namesFromRedis.get();
        }

        log.debug("getMedicalNames: cache miss, loading from db");
        List<String> namesFromDb = repository.getAllNames();
        redisRepository.saveMedicalNames(namesFromDb);

        return namesFromDb;
    }

    private Medical getByIdOrThrowExc(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEDICAL_NOT_FOUND.getErrorMessage(id)));
    }

    private void saveMedicalToUserViewHistory(Medical medical) {
        User user = authUserService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_EMPTY_IN_AUTHORIZE_CONTEXT));

        medicalViewHistoryRepository.save(mvhMapper.toMedicalViewHistory(user, medical));
    }
}
