package org.skyhigh.notesservice.service.user;

import org.skyhigh.notesservice.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.ZonedDateTime;

public interface UserService {
    User save(User user);
    User getUnblockedByUsername(String username);
    User create(User user);
    User getByUsername(String username);
    User getByEmail(String email);
    User getUnblockedByEmail(String email);
    UserDetailsService userDetailsService();
    User getCurrentUser();
    void updateLastLogonDateByUsername(String username, ZonedDateTime lastLogonDate);
}
