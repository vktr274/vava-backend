package sk.vava.zalospevaci.controllers;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import sk.vava.zalospevaci.models.*;
import sk.vava.zalospevaci.services.*;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AppController {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private PhotoService photoService;

    private Object catchtransactionException(TransactionSystemException e) {
        Throwable t = e.getCause();
        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        ConstraintViolationException ex = (ConstraintViolationException) t;
        List<String> exceptions = new ArrayList<>();
        ex.getConstraintViolations().forEach(v -> exceptions.add(v.getMessage()));
        return new ResponseEntity<>(exceptions, HttpStatus.BAD_REQUEST);
    }

    /* USERS calls */

    @GetMapping("/users")
    public Object getAllUsers() {
        try {
            return userService.findAllUsers();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/filters")
    public Object filterUsers(@RequestBody (required = false) JSONObject filters) {
        try {
            return userService.filterUsers(filters);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * GET method to get info about user with {username} specified
    * */
    @GetMapping("/user")
    public Object getUserByLogin(@RequestParam String username)
            throws ResourceNotFoundException {
        try {
            User user = userService.getUserByLogin(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public Object registerUser(@RequestBody (required = false) User user) {
        try {
            if (user.getAddress() != null) {
                addressService.saveAddress(user.getAddress());
            }
            if (user.getPhone() != null) {
                phoneService.savePhone(user.getPhone());
            }
            user = userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{id}")
    public Object delUser(@PathVariable(value = "id") Long userID)
            throws ResourceNotFoundException {
        try {
            User user = userService.getUserById(userID);
            userService.delUser(user);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{id}")
    public Object editUser(@RequestBody (required=false) User user, @PathVariable(value = "id") Long userID) {
        try {
            if (user == null) { // if json body of request is empty
                return new ResponseEntity<>("Body of request cannot be empty and shout contain new user data", HttpStatus.BAD_REQUEST);
            }
            User edit_user = userService.getUserById(userID);
            if (user.getRole() != null && !user.getRole().equals(edit_user.getRole())) { // if we want to change user's role -> error
                return new ResponseEntity<>("Role cannot be changed", HttpStatus.BAD_REQUEST);
            }
            if (user.getUsername() != null) {
                edit_user.setUsername(user.getUsername());
            }
            if (user.getEmail() != null) {
                edit_user.setEmail(user.getEmail());
            }
            if (user.getPassword() != null) {
                edit_user.setPassword(user.getPassword());
            }
            Address old_addr = null;
            if (user.getAddress() != null) {
                old_addr = edit_user.getAddress();
                edit_user.setAddress(user.getAddress());
            }
            Phone old_phone = null;
            if (user.getPhone() != null) {
                old_phone = edit_user.getPhone();
                edit_user.setPhone(user.getPhone());
            }
            if (edit_user.getAddress() != null) { // save new address
                addressService.saveAddress(edit_user.getAddress());
            }
            if (edit_user.getPhone() != null) { //save new phone
                phoneService.savePhone(edit_user.getPhone());
            }
            edit_user = userService.updateUser(edit_user);
            if (old_addr != null) { // delete old address
                addressService.delAddress(old_addr);
            }
            if (old_phone != null) { // delete old phone
                phoneService.delPhone(old_phone);
            }
            return new ResponseEntity<>(edit_user, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    /* ORDERS calls */

    @PostMapping("/orders")
    public Object addOrder(@RequestParam List<Long> mealsId, @RequestParam Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<Item> order_items = new ArrayList<>();
            for (Long id : mealsId) {
                order_items.add(itemService.getItemById(id));
            }
            Order order = new Order();
            order.setUser(user);
            Integer price = 0;
            for (Item item : order_items) {
                price += item.getPrice();
            }
            order.setPrice(price);
            Order result = orderService.saveOrder(order);
            for (Item item : order_items) {
                orderItemService.saveOrderItem(new OrderItem().setDependencies(item, result));
            }

            JSONObject jo = new JSONObject();
            jo.put("price", result.getPrice());
            jo.put("user", result.getUser().getUsername());
            List<String> items_names = new ArrayList<>();
            for (Item item : order_items) {
                items_names.add(item.getName());
            }
            jo.put("order_content", items_names);

            return new ResponseEntity<>(jo, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/orders/{id}")
    public Object deleteOrder(@PathVariable(value = "id") Long orderID) {
        try {
            orderService.deleteOrder(orderID);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /* ITEMS calls */

    @GetMapping("/items/{restaurant_id}")
    public Object getItemsByRestaurant(@PathVariable(value = "restaurant_id") Long restaurID)
            throws ResourceNotFoundException {
        try {

            List<Item> result = itemService.findByRestaurId(restaurID);
            List<JSONObject> resJson = new ArrayList<>();
            for (Item item : result) {
                JSONObject tmp = new JSONObject();
                tmp.put("price", item.getPrice());
                tmp.put("description", item.getDescription());
                tmp.put("name", item.getName());
                tmp.put("restaurant_name", item.getRestaurant().getName());
                resJson.add(tmp);
            }
            return resJson;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/items")
    public Object addProduct(@RequestBody Item item, @RequestParam Long restaurId){
        try {
            item.setRestaurant(restaurantService.getRestaurantById(restaurId));
            itemService.saveItem(item);

            JSONObject jo = new JSONObject();
            jo.put("price", item.getPrice());
            jo.put("description", item.getDescription());
            jo.put("name", item.getName());
            jo.put("restaurant_name", item.getRestaurant().getName());

            return new ResponseEntity<>(jo, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{id}")
    public Object deleteProduct(@PathVariable(value = "id") Long itemID) {
        try {
            itemService.deleteItemById(itemID);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }  catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/item/{id}")
    public Object editItem(@RequestBody (required=false) Item item, @PathVariable(value = "id") Long itemID) {
        try {
            if (item == null) { // if json body of request is empty
                return new ResponseEntity<>("Body of request cannot be empty and shout contain new user data", HttpStatus.BAD_REQUEST);
            }
            Item edit_item = itemService.getItemById(itemID);
            if (item.getName() != null) {
                edit_item.setName(item.getName());
            }
            if (item.getDescription() != null) {
                edit_item.setDescription(item.getDescription());
            }
            if (item.getPrice() != null) {
                edit_item.setPrice(item.getPrice());
            }
            if (item.getRestaurant() != null && item.getRestaurant() != edit_item.getRestaurant()) {
                return new ResponseEntity<>("Restaurant cannot be changed", HttpStatus.BAD_REQUEST);
            }
            Photo old_photo = null;
            if (item.getPhoto() != null) {
                old_photo = edit_item.getPhoto();
                edit_item.setPhoto(item.getPhoto());
            }
            if (edit_item.getPhoto() != null) { // save new photo
                photoService.savePhoto(edit_item.getPhoto());
            }
            edit_item = itemService.saveItem(edit_item);
            if (old_photo != null) { // delete old photo
                photoService.deletePhoto(old_photo);
            }

            JSONObject jo = new JSONObject();
            jo.put("price", edit_item.getPrice());
            jo.put("description", edit_item.getDescription());
            jo.put("name", edit_item.getName());
            jo.put("restaurant_name", edit_item.getRestaurant().getName());

            return new ResponseEntity<>(jo, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    /* RESTAURANTS calls */

    @GetMapping("/restaurants")
    public Object getAllRestaurants() {
        List<Restaurant> result = restaurantService.findAllRestaurants();
        List<JSONObject> resJson = new ArrayList<>();
        for (Restaurant rest : result) {
            JSONObject tmp = new JSONObject();
            tmp.appendField("name", rest.getName());
            tmp.appendField("id", rest.getId());
            tmp.appendField("address", rest.getAddress());
            tmp.appendField("phone", rest.getPhone());
            tmp.appendField("url", rest.getUrl());
            tmp.appendField("blocked", rest.isBlocked());
            resJson.add(tmp);
        }
        return resJson;
    }

    @GetMapping("/restaurants/filters")
    public Object filterRestaurants(@RequestBody (required = false) JSONObject filters) {
        try {
            List<Restaurant> result = restaurantService.filterRestaurants(filters);
            List<JSONObject> resJson = new ArrayList<>();
            for (Restaurant rest : result) {
                JSONObject tmp = new JSONObject();
                tmp.appendField("name", rest.getName());
                tmp.appendField("id", rest.getId());
                tmp.appendField("address", rest.getAddress());
                tmp.appendField("phone", rest.getPhone());
                tmp.appendField("url", rest.getUrl());
                tmp.appendField("blocked", rest.isBlocked());
                resJson.add(tmp);
            }
            return resJson;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/restaurants")
    public Object addRestaurant(@RequestBody Restaurant restaurant, @RequestParam Long managerId) {
        try {
            restaurant.setManager(userService.getUserById(managerId));
            phoneService.savePhone(restaurant.getPhone());
            addressService.saveAddress(restaurant.getAddress());
            Restaurant res = restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (TransactionSystemException e) {
            return catchtransactionException(e);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public Object deleteRestaurant(@PathVariable(value = "id") Long restaurID) {
        try {
            restaurantService.deleteRestaurantById(restaurID);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
