package sk.vava.zalospevaci.services;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
import sk.vava.zalospevaci.artifacts.UserRole;
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
        return userRepository.findById(id).get();
    }

    public User getUserByLogin(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return session.createQuery("SELECT u FROM User u WHERE u.username=:username", User.class).setParameter("username", username).getSingleResult();
    }

    public User updateUser(User user) { return userRepository.save(user); }

    public User saveUser(User user) {
        if (UserRole.contains(user.getRole())){
            return userRepository.save(user);
        } else {
            return null;
        }
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
