package sk.vava.zalospevaci.services;

import net.minidev.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
import sk.vava.zalospevaci.artifacts.UserRole;
import sk.vava.zalospevaci.models.Address;
import sk.vava.zalospevaci.models.Phone;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.UserRepository;

import javax.persistence.criteria.*;
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

    public List<User> filterUsers(JSONObject obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        // Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);

        Predicate usernamePred = builder.and();
        Predicate idPred = builder.and();
        Predicate rolePred = builder.and();
        Predicate addrCityPred = builder.and();

        if (obj.containsKey("id")) {
            idPred = builder.equal(root.get("id"), obj.getAsNumber("id"));
        }

        if (obj.containsKey("username")) {
            usernamePred = builder.like(root.get("username"), obj.getAsString("username")+"%");
        }

        if (obj.containsKey("role")) {
            rolePred = builder.like(root.get("role"), obj.getAsString("role"));
        }

        Join<User, Address> userAddrJoin = null;

        if (obj.containsKey("city")) {
            userAddrJoin = root.join("address");
            addrCityPred = builder.like(userAddrJoin.get("city"), obj.getAsString("city"));
        }

        criteria.where(builder.and(idPred, usernamePred, rolePred, addrCityPred));

        Query<User> query = session.createQuery(criteria);

        return query.getResultList();
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
