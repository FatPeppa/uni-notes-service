package org.skyhigh.notesservice.service.user;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.authentication.exception.UserIsBlockedException;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.entity.User;
import org.skyhigh.notesservice.model.entity.UserProperties;
import org.skyhigh.notesservice.repository.UserPropertiesRepository;
import org.skyhigh.notesservice.repository.UserRepository;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.Flk10000001;
import org.skyhigh.notesservice.validation.flk.Flk10000002;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserPropertiesRepository userPropertiesRepository;

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
}
