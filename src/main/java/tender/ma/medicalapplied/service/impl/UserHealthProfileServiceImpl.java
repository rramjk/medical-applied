package tender.ma.medicalapplied.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tender.ma.medicalapplied.dto.UserHealthProfileDto;
import tender.ma.medicalapplied.dto.UserHealthProfileRequestDto;
import tender.ma.medicalapplied.exceptions.BadRequestException;
import tender.ma.medicalapplied.exceptions.ErrorCode;
import tender.ma.medicalapplied.exceptions.NotFoundException;
import tender.ma.medicalapplied.model.mapping.UserHealthProfileMapper;
import tender.ma.medicalapplied.model.profile.UserHealthProfile;
import tender.ma.medicalapplied.model.user.User;
import tender.ma.medicalapplied.repository.UserHealthProfileRepository;
import tender.ma.medicalapplied.repository.UserRepository;
import tender.ma.medicalapplied.service.UserHealthProfileService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserHealthProfileServiceImpl implements UserHealthProfileService {
    private final UserHealthProfileRepository repository;
    private final UserHealthProfileMapper mapper;
    private final UserRepository userRepository;

    @Override
    public UserHealthProfileDto getUserHealthProfileByUserId(UUID id) {
        log.info("getUserHealthProfileByUserId: get user health profile for user id {}", id);
        return mapper.toDto(getProfileByUserId(id));
    }

    @Override
    public UserHealthProfileDto createUserHealthProfile(UUID userId, @Valid UserHealthProfileRequestDto requestDto) {
        log.info("createUserHealthProfile: create user health profile for user id {}", userId);
        User user = getUserById(userId);

        Optional<UserHealthProfile> userHealthProfile = repository.getUserHealthProfileByUserId(user.getId());
        if (userHealthProfile.isEmpty()) {
            return mapper.toDto(
                    repository.save(getUserHealthProfileByUserAndRequest(user, requestDto))
            );
        } else {
            throw new BadRequestException(ErrorCode.USER_HEALTH_PROFILE_ALREADY_EXISTS_FOR_USER.getErrorMessage(userId));
        }
    }

    @Override
    public UserHealthProfileDto updateUserHealthProfile(UUID userId, UUID healthId, @Valid UserHealthProfileRequestDto userHealthRequestDto) {
        log.info("updateUserHealthProfile: update user health profile for user id {} and health id {}", userId, healthId);
        UserHealthProfile healthProfile = getProfileAndCheckUserIsLinkedTo(userId, healthId);
        UserHealthProfile updatedProfile = mapper.toUpdateEntity(userHealthRequestDto, healthProfile);
        return mapper.toDto(repository.save(updatedProfile));
    }

    @Override
    public UserHealthProfileDto getUserHealthProfile(UUID userId, UUID healthId) {
        log.info("getUserHealthProfile: get user health profile for user id {} and health id {}", userId, healthId);
        return mapper.toDto(getProfileAndCheckUserIsLinkedTo(userId, healthId));
    }

    @Override
    public UserHealthProfileDto deleteUserHealthProfile(UUID userId, UUID healthId) {
        log.info("deleteUserHealthProfile: delete user health profile for user id {} and health id {}", userId, healthId);
        UserHealthProfile healthProfileToDelete = getProfileAndCheckUserIsLinkedTo(userId, healthId);
        repository.deleteById(healthProfileToDelete.getId());
        return mapper.toDto(healthProfileToDelete);
    }

    private UserHealthProfile getUserHealthProfileByUserAndRequest(User user, UserHealthProfileRequestDto request) {
        UserHealthProfile entity = mapper.toEntity(request);
        entity.setUser(user);

        return entity;
    }

    private UserHealthProfile getHealthProfileByIdOrElseThrow(UUID healthId) {
        return repository.findById(healthId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_HEALTH_PROFILE_NOT_FOUND.getErrorMessage(healthId)));
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.getErrorMessage(id)));
    }

    private UserHealthProfile getProfileAndCheckUserIsLinkedTo(UUID userId, UUID healthId) {
        User user = getUserById(userId);
        UserHealthProfile healthProfile = getHealthProfileByIdOrElseThrow(healthId);

        if (!healthProfile.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(ErrorCode.USER_HEALTH_PROFILE_AND_USER_NOT_LINKED.getErrorMessage(healthId, userId));
        }
        return healthProfile;
    }

    private UserHealthProfile getProfileByUserId(UUID id) {
        return repository.getUserHealthProfileByUserId(getUserById(id).getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_HEALTH_PROFILE_NOT_FOUND_FOR_USER.getErrorMessage(id)));

    }

}
