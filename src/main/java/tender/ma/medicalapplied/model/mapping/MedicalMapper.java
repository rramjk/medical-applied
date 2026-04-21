package tender.ma.medicalapplied.model.mapping;

import org.mapstruct.Mapper;
import tender.ma.medicalapplied.dto.MedicalDto;
import tender.ma.medicalapplied.model.medical.Medical;

@Mapper
public interface MedicalMapper {

    MedicalDto toDto(Medical medical);
}
