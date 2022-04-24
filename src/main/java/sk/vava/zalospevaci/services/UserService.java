package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.UserRole;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Address;
import sk.vava.zalospevaci.models.Phone;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.UserRepository;

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

    public User getUserByUsername(String username) throws NotFoundException {
        var user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) {
            throw new NotFoundException(username + " not found");
        }
        return user;
    }

    public Page<User> getByAll(String namePart, String role, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByRoleAndUsernameContainingAndBlocked(role, namePart, blocked,
                pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with '" + namePart + "' in their username and role " + role + " and status blocked = " +
                            blocked + " not found"
            );
        }
        return users;
    }

    public Page<User> getByNameAndRole(String namePart, String role, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByRoleAndUsernameContaining(role, namePart,
                pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with '" + namePart + "' in their username and role '" + role + "' not found"
            );
        }
        return users;
    }

    public Page<User> getByRoleAndBlocked( String role, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByRoleAndBlocked(role, blocked,
                pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with role '" + role + "' and status blocked = " + blocked + " not found"
            );
        }
        return users;
    }

    public Page<User> getByNameAndBlocked( String namePart, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var users = userRepository.findAllByUsernameContainingAndBlocked(namePart, blocked,
                pageable).orElse(null);
        if (users == null) {
            throw new NotFoundException(
                    "users with '" + namePart + "' in their username and status blocked = " + blocked + " not found"
            );
        }
        return users;
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
