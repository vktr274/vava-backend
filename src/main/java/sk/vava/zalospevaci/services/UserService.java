package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.models.Address;
import sk.vava.zalospevaci.models.Phone;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PhoneService phoneService;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void delUser(User user) {
        userRepository.delete(user);
        Address address = user.getAddress();
        if (address != null){
            addressService.delAddress(user.getAddress());
        }
        Phone phone = user.getPhone();
        if (phone != null){
            phoneService.delPhone(user.getPhone());
        }
    }
}
