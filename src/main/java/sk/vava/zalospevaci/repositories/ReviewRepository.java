package sk.vava.zalospevaci.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Restaurant;
import sk.vava.zalospevaci.models.Review;
import sk.vava.zalospevaci.models.User;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Page<Review>> findAllByRestaurantAndUser(Restaurant restaurant, User user, Pageable pageable);
    Optional<Page<Review>> findAllByRestaurant(Restaurant restaurant, Pageable pageable);
    Optional<Page<Review>> findAllByUser(User user, Pageable pageable);
    Optional<Review> findByIdAndUser(Long id, User user);
}
