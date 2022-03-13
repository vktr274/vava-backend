package sk.vava.zalospevaci.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
