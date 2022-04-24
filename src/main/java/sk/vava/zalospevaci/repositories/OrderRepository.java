package sk.vava.zalospevaci.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Order;
import sk.vava.zalospevaci.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findAllByUser(User user);
    Optional<Page<Order>> findAllByUser(User user, Pageable pageable);
}
