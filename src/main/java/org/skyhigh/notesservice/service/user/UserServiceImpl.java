package org.skyhigh.notesservice.service.user;

import org.shyhigh.grpc.notes.SearchUsersDateFilterType;
import org.shyhigh.grpc.notes.SearchUsersRequest;
import org.skyhigh.notesservice.authentication.exception.UserIsBlockedException;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.entity.Role;
import org.skyhigh.notesservice.model.entity.User;
import org.skyhigh.notesservice.model.entity.UserProperties;
import org.skyhigh.notesservice.repository.UserPropertiesRepository;
import org.skyhigh.notesservice.repository.UserRepository;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;
import org.skyhigh.notesservice.validation.flk.flk1000.Flk10000001;
import org.skyhigh.notesservice.validation.flk.flk1000.Flk10000002;
import org.skyhigh.notesservice.validation.flk.flk1001.Flk10010000;
import org.skyhigh.notesservice.validation.flk.flk1001.Flk10010001;
import org.skyhigh.notesservice.validation.flk.flk1001.Flk10010002;
import org.skyhigh.notesservice.validation.flk.flk1001.Flk10010003;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserPropertiesRepository userPropertiesRepository;
    private final boolean adminBlockOptionOn;

    public UserServiceImpl(
            UserRepository userRepository,
            UserPropertiesRepository userPropertiesRepository,
            @Qualifier("AdminBlockOptionOn") boolean adminBlockOptionOn
    ) {
        this.userRepository = userRepository;
        this.userPropertiesRepository = userPropertiesRepository;
        this.adminBlockOptionOn = adminBlockOptionOn;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"CurrentUserCache", "UserByEmailCache", "UserByUsernameCache"}, allEntries = true)
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw FlkException.builder()
                    .flkCode(Flk10000001.getCode())
                    .flkMessage(Flk10000001.getMessage())
                    .flkParameterName(Flk10000001.getFieldName())
                    .build();
        }

        user.setEmail(user.getEmail().toLowerCase());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw FlkException.builder()
                    .flkCode(Flk10000002.getCode())
                    .flkMessage(Flk10000002.getMessage())
                    .flkParameterName(Flk10000002.getFieldName())
                    .build();
        }

        user = save(user);

        userPropertiesRepository.save(UserProperties.builder()
                .id(null)
                .userId(user.getId())
                .lastNotesCreatedDateSortDirection(SortDirection.DESC)
                .build());

        return user;
    }

    @Override
    public User getByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException("User not found for username " + username);
        return user.get();
    }

    @Override
    public User getByEmail(String email) {
        var user = userRepository.findByEmail(email.toLowerCase());
        if (user.isEmpty()) throw new UsernameNotFoundException("User not found for email " + email);
        return user.get();
    }

    @Override
    @Cacheable(value = "UserByUsernameCache", unless = "#result == null")
    public User getUnblockedByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException("User not found for username " + username);
        if (user.get().isBlocked()) throw new UserIsBlockedException("Found user is blocked and cannot be shown");
        return user.get();
    }

    @Override
    @Cacheable(value = "UserByEmailCache", unless = "#result == null")
    public User getUnblockedByEmail(String email) {
        var user = userRepository.findByEmail(email.toLowerCase());
        if (user.isEmpty()) throw new UsernameNotFoundException("User not found for email " + email);
        if (user.get().isBlocked()) throw new UserIsBlockedException("Found user is blocked and cannot be shown");
        return user.get();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getUnblockedByUsername;
    }

    @Override
    @Cacheable(value = "CurrentUserCache", unless = "#result == null")
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUnblockedByUsername(username);
    }

    @Override
    @CacheEvict(value = {"CurrentUserCache", "UserByEmailCache", "UserByUsernameCache"}, allEntries = true)
    public void updateLastLogonDateByUsername(String username, ZonedDateTime lastLogonDate) {
        userRepository.updateLastLogonDateByUsername(username, lastLogonDate);
    }

    @Override
    public List<User> searchUsers(SearchUsersRequest searchUsersRequest) {
        List<User> users = new ArrayList<>();
        if (searchUsersRequest.hasUserId() && (searchUsersRequest.hasUsername()))
            users = userRepository.findByIdAndUsername(
                    searchUsersRequest.getUserId().getValue(),
                    searchUsersRequest.getUsername().getValue()
            ).map(List::of).orElse(null);
        else if (searchUsersRequest.hasUserId())
            users = userRepository.findById(searchUsersRequest.getUserId()
                    .getValue()).map(List::of).orElse(null);
        else if (searchUsersRequest.hasUsername())
            users = userRepository.findByUsername(searchUsersRequest.getUsername()
                    .getValue()).map(List::of).orElse(null);

        if (users != null && users.isEmpty()) users = userRepository.findAllSortedByRegisterDateDesc();
        return users == null ? List.of() : users.stream()
                .filter(x -> !searchUsersRequest.hasEmail() || x.getEmail().equals(searchUsersRequest.getEmail().getValue()))
                .filter(x -> !searchUsersRequest.hasRole() || x.getRole().toString().equals(searchUsersRequest.getRole().getValue()))
                .filter(x -> !searchUsersRequest.hasBlocked() || x.isBlocked() == searchUsersRequest.getBlocked().getValue())
                .filter(x -> !searchUsersRequest.hasClientId() || x.getClientId().toString().equals(searchUsersRequest.getClientId().getValue()))
                .filter(x -> {
                    if (searchUsersRequest.hasBeginDate())
                        return searchUsersRequest.getDateFilterType() == SearchUsersDateFilterType.BY_REGISTER_DATE
                                ? x.getRegisterDate().isAfter(ZonedDateTime
                                    .parse(searchUsersRequest.getBeginDate().getValue()))
                                : x.getLastLogonDate().isAfter(ZonedDateTime
                                    .parse(searchUsersRequest.getBeginDate().getValue()));
                    return true;
                })
                .filter(x -> {
                    if (searchUsersRequest.hasEndDate())
                        return searchUsersRequest.getDateFilterType() == SearchUsersDateFilterType.BY_REGISTER_DATE
                                ? x.getRegisterDate().isBefore(ZonedDateTime
                                .parse(searchUsersRequest.getEndDate().getValue()))
                                : x.getLastLogonDate().isBefore(ZonedDateTime
                                .parse(searchUsersRequest.getEndDate().getValue()));
                    return true;
                })
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"CurrentUserCache", "UserByEmailCache", "UserByUsernameCache"}, allEntries = true)
    public void blockUser(Long initiatorId, Long userId) {
        if (initiatorId.equals(userId))
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10010002.getCode())
                    .flkMessage(Flk10010002.getMessage())
                    .build()));

        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10010000.getCode())
                    .flkMessage(Flk10010000.getMessage())
                    .build()));
        if (user.get().isBlocked())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10010001.getCode())
                    .flkMessage(Flk10010001.getMessage())
                    .build()));
        if (adminBlockOptionOn && user.get().getRole().equals(Role.ROLE_ADMIN))
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10010003.getCode())
                    .flkMessage(Flk10010003.getMessage())
                    .build()));

        userRepository.blockUserById(userId, ZonedDateTime.now());
    }
}
