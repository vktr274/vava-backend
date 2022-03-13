package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.models.Phone;
import sk.vava.zalospevaci.repositories.PhoneRepository;

import java.util.List;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;

    public List<Phone> findAllPhones() {
        return phoneRepository.findAll();
    }

    public Phone savePhone(Phone phone) {
        return phoneRepository.save(phone);
    }
}
