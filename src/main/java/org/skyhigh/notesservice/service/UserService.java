package org.skyhigh.notesservice.service;

import org.skyhigh.notesservice.data.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    User save(User user);
    User getByUsername(String username);
    User create(User user);
    User getByEmail(String email);
    UserDetailsService userDetailsService();
    User getCurrentUser();
}
