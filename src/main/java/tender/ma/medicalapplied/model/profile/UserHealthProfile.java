package tender.ma.medicalapplied.model.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tender.ma.medicalapplied.model.user.AuditableEntity;
import tender.ma.medicalapplied.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ma_user_health_profiles")
public class UserHealthProfile extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "weight")
    private Double weight;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chronic_conditions", columnDefinition = "jsonb")
    private List<String> chronicConditions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "health_features", columnDefinition = "jsonb")
    private List<String> healthFeatures = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allergies", columnDefinition = "jsonb")
    private List<String> allergies = new ArrayList<>();
}
