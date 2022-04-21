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
    Optional<Page<User>> findAllByRole(String role, Pageable pageable);
    Optional<Page<User>> findAllByBlocked(Boolean blocked, Pageable pageable);
    Optional<Page<User>> findByUsernameContaining(String usernamePart, Pageable pageable);
}
