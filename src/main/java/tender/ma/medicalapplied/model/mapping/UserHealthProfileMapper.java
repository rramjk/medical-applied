package tender.ma.medicalapplied.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tender.ma.medicalapplied.dto.UserHealthProfileDto;
import tender.ma.medicalapplied.dto.UserHealthProfileRequestDto;
import tender.ma.medicalapplied.model.profile.UserHealthProfile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapper
public interface UserHealthProfileMapper {

    @Mapping(target = "userId", source = "userHealthProfile.user.id")
    UserHealthProfileDto toDto(UserHealthProfile userHealthProfile);

    UserHealthProfile toEntity(UserHealthProfileRequestDto userHealthProfileRequestDto);

    @IgnoreFields
    UserHealthProfile toUpdateEntity(UserHealthProfileRequestDto userHealthProfileRequestDto, @MappingTarget UserHealthProfile userHealthProfile);

    @Retention(RetentionPolicy.CLASS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @interface IgnoreFields {
    }
}
