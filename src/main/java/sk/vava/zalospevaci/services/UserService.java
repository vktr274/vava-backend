package sk.vava.zalospevaci.services;

import net.minidev.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
import sk.vava.zalospevaci.artifacts.UserRole;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.*;
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

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) throws NotFoundException {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return user;
    }

    public User getUserByBasicAuth(Long id, String basicAuthToken) throws NotFoundException, NotAuthorizedException {
        var user = getUserById(id);
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

    public Page<User> getByName(String namePart, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findByUsernameContaining(namePart, pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with '" + namePart + "' in their username not found"
            );
        }
        return users;
    }

    public Page<User> getByRole(String role, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByRole(role, pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users for role '" + role + "' not found"
            );
        }
        return users;
    }

    public Page<User> getByStatus(Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByBlocked(blocked, pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with status blocked = '" + blocked + "' not found"
            );
        }
        return users;
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
