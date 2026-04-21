package tender.ma.medicalapplied.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tender.ma.medicalapplied.model.profile.UserHealthProfile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserHealthProfileRepository extends JpaRepository<UserHealthProfile, UUID> {

    Optional<UserHealthProfile> getUserHealthProfileByUserId(UUID id);
}
