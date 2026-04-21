package tender.ma.medicalapplied.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tender.ma.medicalapplied.model.user.Role;
import tender.ma.medicalapplied.model.user.RoleTypes;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(RoleTypes name);
}
