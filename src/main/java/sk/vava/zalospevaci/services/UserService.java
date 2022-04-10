package sk.vava.zalospevaci.services;

import net.minidev.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
import sk.vava.zalospevaci.artifacts.UserRole;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Address;
import sk.vava.zalospevaci.models.Phone;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.UserRepository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.nio.charset.Charset;
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

    public User getUserById(Long id) throws NotFoundException {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return user;
    }

    public User getUserByBasicAuth(String username, String basicAuthToken) throws NotFoundException, NotAuthorizedException {
        var user = getUserByUsername(username);
        if (!HttpHeaders.encodeBasicAuth(user.getUsername(), user.getPassword(), null).equals(basicAuthToken)) {
            throw new NotAuthorizedException("not authorized");
        }
        return user;
    }

    public User getUserByUsername(String username) throws NotFoundException {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new NotFoundException(username + " not found");
        }
        return user;
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

        if (obj.containsKey("city")) {
            Join<User, Address> userAddrJoin = root.join("address");
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
