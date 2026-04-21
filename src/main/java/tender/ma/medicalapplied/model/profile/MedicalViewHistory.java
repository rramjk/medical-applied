package tender.ma.medicalapplied.model.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tender.ma.medicalapplied.model.medical.Medical;
import tender.ma.medicalapplied.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ma_medical_views_history")
public class MedicalViewHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medical_id")
    private Medical medical;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;
}