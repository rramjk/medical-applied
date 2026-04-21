package tender.ma.medicalapplied.model.mapping;

import org.mapstruct.Mapper;
import tender.ma.medicalapplied.dto.EmailVerificationDto;
import tender.ma.medicalapplied.model.profile.EmailVerification;

@Mapper
public interface EmailVerificationMapper {

    EmailVerificationDto toDto(EmailVerification entity);

    EmailVerification toEntity(EmailVerificationDto entity);
}
