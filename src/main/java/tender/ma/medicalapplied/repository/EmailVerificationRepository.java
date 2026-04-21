package tender.ma.medicalapplied.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tender.ma.medicalapplied.model.profile.EmailVerification;
import tender.ma.medicalapplied.model.profile.EmailVerificationStatus;
import tender.ma.medicalapplied.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> getEmailVerificationByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerification e WHERE e.expired <= :now OR e.status = :status")
    void deleteExpiredRequestsOrEqualsStatus(LocalDateTime now, EmailVerificationStatus status);

    @Query("FROM EmailVerification e WHERE e.expired > :now AND e.status = :status")
    List<EmailVerification> getEmailVerificationByStatus(LocalDateTime now, EmailVerificationStatus status);

    @Modifying
    @Transactional
    @Query("""
       UPDATE EmailVerification e
       SET e.status = :newStatus
       WHERE e.id = :id
         AND e.status = :expectedStatus
         AND e.expired > :now
       """)
    int updateStatusIfCurrent(UUID id,
                              EmailVerificationStatus expectedStatus,
                              EmailVerificationStatus newStatus,
                              LocalDateTime now);
}
