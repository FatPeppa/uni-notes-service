package org.skyhigh.notesservice.service;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.entity.User;
import org.skyhigh.notesservice.repository.UserRepository;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.Flk10000001;
import org.skyhigh.notesservice.validation.flk.Flk10000002;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            //throw new UsernameNotFoundException("User not found for email " + user.getUsername());
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

        return save(user);
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
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}
