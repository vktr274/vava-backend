package sk.vava.zalospevaci.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.ReviewPhoto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    Optional<List<ReviewPhoto>> findAllByReviewId(Long reviewID);
}
