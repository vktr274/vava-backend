package sk.vava.zalospevaci.controllers;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import sk.vava.zalospevaci.models.*;
import sk.vava.zalospevaci.services.*;

import java.util.ArrayList;
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
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/register")
    public Map<String, User> registerUser(@RequestBody User user) {
        if (user.getAddress() != null) {
            addressService.saveAddress(user.getAddress());
        }
        if (user.getPhone() != null) {
            phoneService.savePhone(user.getPhone());
        }
        userService.saveUser(user);
        Map<String, User> response = new HashMap<>();
        response.put("Registered", user);
        return response;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Boolean> delUser(@PathVariable(value = "id") Long userID)
            throws ResourceNotFoundException {
        User user = userService.getUserById(userID);
        userService.delUser(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted", Boolean.TRUE);
        return response;
    }

    @PostMapping("/orders")
    public JSONObject addOrder(@RequestParam List<Long> mealsId, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<Item> order_items = new ArrayList<>();
        for (Long id : mealsId){
            order_items.add(itemService.getItemById(id));
        }
        Order order = new Order();
        order.setUser(user);
        Integer price = 0;
        for (Item item : order_items){
            price += item.getPrice();
        }
        order.setPrice(price);
        Order result = orderService.saveOrder(order);
        for (Item item : order_items){
            orderItemService.saveOrderItem(new OrderItem().setDependencies(item, result));
//            result.addMeal(res);
        }

        JSONObject jo = new JSONObject();
        jo.put("price", result.getPrice());
        jo.put("user", result.getUser().getUsername());
        List <String> items_names = new ArrayList<>();
        for (Item item : order_items){
            items_names.add(item.getName());
        }
        jo.put("order_content", items_names);

        return jo;
    }

    @DeleteMapping("/orders/{id}")
    public boolean deleteOrder(@PathVariable(value = "id") Long orderID) {
        orderService.deleteOrder(orderID);
        return true;
    }

    @PostMapping("/items")
    public JSONObject addProduct(@RequestBody Item item, @RequestParam Long restaurId) {
        item.setRestaurant(restaurantService.getRestaurantById(restaurId));
        itemService.saveItem(item);

        JSONObject jo = new JSONObject();
        jo.put("price", item.getPrice());
        jo.put("description", item.getDescription());
        jo.put("name", item.getName());
        jo.put("restaurant_name", item.getRestaurant().getName());

        return jo;
    }

    @DeleteMapping("/items/{id}")
    public boolean deleteProduct(@PathVariable(value = "id") Long itemID) {
        itemService.deleteItem(itemID);
        return true;
    }

    @PostMapping("/restaurants")
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant, @RequestParam Long managerId) {
        restaurant.setManager(userService.getUserById(managerId));
        phoneService.savePhone(restaurant.getPhone());
        addressService.saveAddress(restaurant.getAddress());
        return restaurantService.saveRestaurant(restaurant);
    }

    @DeleteMapping("/restaurants/{id}")
    public boolean deleteRestaurant(@PathVariable(value = "id") Long itemID) {
        itemService.deleteItem(itemID);
        return true;
    }
}
