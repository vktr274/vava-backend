package sk.vava.zalospevaci.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    Optional<Page<User>> findAllByRoleAndUsernameContainingAndBlocked(String role, String usernamePart, Boolean blocked,
                                                            Pageable pageable);
    Optional<Page<User>> findAllByRoleAndUsernameContaining(String role, String usernamePart, Pageable pageable);
    Optional<Page<User>> findAllByRoleAndBlocked(String role, Boolean blocked, Pageable pageable);
    Optional<Page<User>> findAllByUsernameContainingAndBlocked(String usernamePart, Boolean blocked, Pageable pageable);
    Optional<Page<User>> findAllByRole(String role, Pageable pageable);
    Optional<Page<User>> findAllByBlocked(Boolean blocked, Pageable pageable);
    Optional<Page<User>> findByUsernameContaining(String usernamePart, Pageable pageable);
}
