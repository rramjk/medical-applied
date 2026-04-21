package tender.ma.medicalapplied.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tender.ma.medicalapplied.model.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.emailVerified=true WHERE u.id=:userId")
    int updateUserEmailVerifiedStatusToTrue(UUID userId);
}
