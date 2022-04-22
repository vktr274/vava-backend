package sk.vava.zalospevaci.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Restaurant;
import sk.vava.zalospevaci.models.User;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Page<Restaurant>> findAllByNameContainingAndBlocked(String namePart, Boolean blocked, Pageable pageable);
    Optional<Page<Restaurant>> findAllByBlocked(Boolean blocked, Pageable pageable);
    Optional<Page<Restaurant>> findByNameContaining(String namePart, Pageable pageable);
    Optional<Page<Restaurant>> findByManager(User manager, Pageable pageable);

    @Query("select r from Restaurant r join r.address a where r.blocked = ?2 and r.name like %?3% and a.city like %?1%")
    Optional<Page<Restaurant>> findByAddressAndBlockedAndName(String city, Boolean blocked, String namePart, Pageable pageable);

    @Query("select r from Restaurant r join r.address a where a.city like %?1% and r.blocked = ?2")
    Optional<Page<Restaurant>> findByAddressAndBlocked(String city, Boolean blocked, Pageable pageable);

    @Query("select r from Restaurant r join r.address a where r.name like %?2% and a.city like %?1%")
    Optional<Page<Restaurant>> findByAddressAndName(String city, String namePart, Pageable pageable);

    @Query("select r from Restaurant r join r.address a where a.city like %?1%")
    Optional<Page<Restaurant>> findByAddress(String city, Pageable pageable);
}
