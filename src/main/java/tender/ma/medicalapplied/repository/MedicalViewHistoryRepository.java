package tender.ma.medicalapplied.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tender.ma.medicalapplied.model.medical.Medical;
import tender.ma.medicalapplied.model.profile.MedicalViewHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalViewHistoryRepository extends JpaRepository<MedicalViewHistory, UUID> {

    List<MedicalViewHistory> getMedicalViewHistoryByUserId(UUID userId);

    @Query("""
            select m
            from MedicalViewHistory mvh
            join mvh.medical m
            where mvh.user.id = :userId
            """)
    List<Medical> getMedicalsFromViewsHistoryByUserId(UUID userId);

    @Modifying
    @Transactional
    void deleteMedicalViewHistoryByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MedicalViewHistory WHERE viewedAt < :expiredDate")
    void deleteExpiredViews(LocalDateTime expiredDate);
}
