package tender.ma.medicalapplied.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${service.redis.duration}")
    private Duration redisDuration;

    private ValueOperations<String, String> values() {
        return redisTemplate.opsForValue();
    }

    public void save(String key, Object value) {
        log.debug("save: key={}", key);
        try {
            values().set(key, objectMapper.writeValueAsString(value), redisDuration);
        } catch (Exception e) {
            log.error("save: throws error when save value with key: {}", key);
        }

    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        log.debug("get: key={}", key);
        try {
            Object value = objectMapper.readValue(values().get(key), clazz);
            if (clazz.isInstance(value)) {
                return Optional.of(clazz.cast(value));
            } else {
                log.error("get: object with key={} is incorrected", key);
                return Optional.empty();
            }
        }  catch (Exception e) {
            log.error("get: throws error when save value with key: {}", key);
            return Optional.empty();
        }

    }

    public <T> Optional<List<T>> getList(String key, Class<T> clazzOfList) {
        try {
            Object value = objectMapper.readValue(values().get(key), getJavaTypeByClass(clazzOfList));
            if (!(value instanceof List<?>)) {
                log.error("getList: object with key={} is not list", key);
                return Optional.empty();
            } else if (!objectListIsEqualsClass(value, clazzOfList)) {
                log.error("getList: object with key={} is not equals class", key);
                return Optional.empty();
            } else {
                return Optional.of(mapObjectListToClass(value, clazzOfList));
            }
        }  catch (Exception e) {
            log.error("getList: throws error when save value with key: {}", key);
            return Optional.empty();
        }

    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean expire(String key) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, redisDuration));
    }

    private <T> boolean objectListIsEqualsClass(Object value, Class<T> clazz) {
        if (!(value instanceof List<?> list)) {
            return false;
        }

        return list.stream()
                .filter(Objects::nonNull)
                .allMatch(clazz::isInstance);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> mapObjectListToClass(Object value, Class<T> clazzOfList) {
        return ((List<Object>) value).stream().map(clazzOfList::cast).toList();
    }

    private <T> JavaType getJavaTypeByClass(Class<T> tClass) {
        return objectMapper.getTypeFactory()
                .constructCollectionType(List.class, tClass);
    }

}
