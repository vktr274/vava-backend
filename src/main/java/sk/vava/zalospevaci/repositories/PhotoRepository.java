package sk.vava.zalospevaci.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
