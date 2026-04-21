package tender.ma.medicalapplied.model.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tender.ma.medicalapplied.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ma_email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expired")
    private LocalDateTime expired;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus status;

    @Column(name = "verification_token")
    private UUID verificationToken;
}
