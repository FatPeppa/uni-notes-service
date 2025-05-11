package org.skyhigh.notesservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notesservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByIdAndUsername(Long id, String username);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.users SET last_logon_date = ?2 WHERE username = ?1", nativeQuery = true)
    void updateLastLogonDateByUsername(String username, ZonedDateTime lastLogonDate);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.users SET blocked = TRUE, last_logon_date = ?2 WHERE id = ?1", nativeQuery = true)
    void blockUserById(Long userId, ZonedDateTime lastLogonDate);

    @Query(value = "SELECT * FROM public.users ORDER BY register_date DESC", nativeQuery = true)
    List<User> findAllSortedByRegisterDateDesc();
}
