package tender.ma.medicalapplied.repository.medical;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tender.ma.medicalapplied.model.medical.Medical;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@NoArgsConstructor
public final class MedicalSpecification {

    public static Specification<Medical> byFilters(String countryEn, String type, String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(countryEn)) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("countryEn")),
                                countryEn.trim().toLowerCase(Locale.ROOT)
                        )
                );
            }

            if (StringUtils.hasText(type)) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("type")),
                                type.trim().toLowerCase(Locale.ROOT)
                        )
                );
            }

            if (StringUtils.hasText(name)) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + name.trim().toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}