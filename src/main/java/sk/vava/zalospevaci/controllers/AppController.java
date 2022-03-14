package sk.vava.zalospevaci.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.services.AddressService;
import sk.vava.zalospevaci.services.PhoneService;
import sk.vava.zalospevaci.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppController {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PhoneService phoneService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/register")
    public Map<String, User> addProduct(@RequestBody User user) {
        addressService.saveAddress(user.getAddress());
        phoneService.savePhone(user.getPhone());
        userService.saveUser(user);
        Map<String, User> response = new HashMap<>();
        response.put("Registered", user);
        return response;
    }

    @DeleteMapping("/delUser/{id}")
    public Map<String, Boolean> delUser(@PathVariable(value = "id") Long userID)
            throws ResourceNotFoundException {
        User user = userService.getUserById(userID);
        userService.delUser(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted", Boolean.TRUE);
        return response;
    }
}
