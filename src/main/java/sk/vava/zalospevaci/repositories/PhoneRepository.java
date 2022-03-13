package sk.vava.zalospevaci.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Phone;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

}
