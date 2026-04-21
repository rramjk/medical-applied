package tender.ma.medicalapplied.repository.medical;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tender.ma.medicalapplied.model.medical.Medical;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRepository extends JpaRepository<Medical, UUID>, JpaSpecificationExecutor<Medical> {

    @Query("SELECT DISTINCT countryRu FROM Medical")
    List<String> getAllCountriesRu();

    @Query("SELECT DISTINCT countryEn FROM Medical")
    List<String> getAllCountriesEn();

    @Query("SELECT DISTINCT type FROM Medical ")
    List<String> getAllTypes();

    @Query("SELECT DISTINCT name FROM Medical")
    List<String> getAllNames();


}
