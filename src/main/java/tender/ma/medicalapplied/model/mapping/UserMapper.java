package tender.ma.medicalapplied.model.mapping;

import org.mapstruct.*;
import tender.ma.medicalapplied.dto.UserRequestDto;
import tender.ma.medicalapplied.dto.UserDto;
import tender.ma.medicalapplied.model.user.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapper
public interface UserMapper {

    @Mapping(target = "roleId", source = "user.role.id")
    @Mapping(target = "roleName", source = "user.role.name")
    @Mapping(target = "roleDescription", source = "user.role.description")
    UserDto toDto(User user);

    @IgnoreFields
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "userConsent", ignore = true)
    @Mapping(target = "privacyConsent", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUpdatedEntity(User updatedEntity, @MappingTarget User mainEntity);

    User toEntityFromRequestDto(UserRequestDto userRequestDto);

    @Retention(RetentionPolicy.CLASS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @interface IgnoreFields {
    }
}
