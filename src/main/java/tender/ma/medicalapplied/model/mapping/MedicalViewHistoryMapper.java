package tender.ma.medicalapplied.model.mapping;

import org.mapstruct.Mapper;
import tender.ma.medicalapplied.model.medical.Medical;
import tender.ma.medicalapplied.model.profile.MedicalViewHistory;
import tender.ma.medicalapplied.model.user.User;

import java.time.LocalDateTime;

@Mapper
public interface MedicalViewHistoryMapper {

    default MedicalViewHistory toMedicalViewHistory(User user, Medical medical) {
        MedicalViewHistory mvh = new MedicalViewHistory();
        mvh.setUser(user);
        mvh.setMedical(medical);
        mvh.setViewedAt(LocalDateTime.now());

        return mvh;
    }
}
