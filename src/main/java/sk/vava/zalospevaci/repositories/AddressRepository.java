package sk.vava.zalospevaci.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.vava.zalospevaci.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
