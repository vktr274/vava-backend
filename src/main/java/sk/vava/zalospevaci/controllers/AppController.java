package sk.vava.zalospevaci.controllers;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.*;
import sk.vava.zalospevaci.services.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class AppController {
    static final private Path MEDIA_ROOT = Path.of(System.getProperty("user.dir"), "media");

    @Autowired private UserService userService;
    @Autowired private AddressService addressService;
    @Autowired private PhoneService phoneService;
    @Autowired private OrderService orderService;
    @Autowired private ItemService itemService;
    @Autowired private OrderItemService orderItemService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private PhotoService photoService;
    @Autowired private ReviewPhotoService reviewPhotoService;
    @Autowired private ReviewService reviewService;

    /* USERS calls */

    @GetMapping("/users")
    public ResponseEntity<List<User>> filterUsers(@RequestBody (required = false) JSONObject filters) {
        try {
            return new ResponseEntity<>(
                    filters == null ? userService.findAllUsers() : userService.filterUsers(filters),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * GET method to get info about user with {username} specified
    * */
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByLogin(@PathVariable(value = "username") String username)
            throws ResourceNotFoundException {
        try {
            User user = userService.getUserByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> registerUser(@RequestBody (required = false) User user) {
        try {
            if (user.getAddress() != null) {
                addressService.saveAddress(user.getAddress());
            }
            if (user.getPhone() != null) {
                phoneService.savePhone(user.getPhone());
            }
            user = userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> delUser(@PathVariable(value = "id") Long userID)
            throws ResourceNotFoundException {
        try {
            User user = userService.getUserById(userID);
            userService.delUser(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> editUser(@RequestBody User user, @PathVariable(value = "id") Long userID) {
        try {
            User editUser = userService.getUserById(userID);
            if (user.getUsername() != null) {
                editUser.setUsername(user.getUsername());
            }
            if (user.getEmail() != null) {
                editUser.setEmail(user.getEmail());
            }
            if (user.getPassword() != null) {
                editUser.setPassword(user.getPassword());
            }
            if (user.getAddress() != null) {
                Address newAddr = user.getAddress();
                Address oldAddr = editUser.getAddress();
                if (oldAddr != null) { // if user had phone before
                    oldAddr.setData(newAddr);
                    addressService.saveAddress(oldAddr);
                } else { // if there was no phone
                    editUser.setAddress(addressService.saveAddress(user.getAddress()));
                }
            }
            if (user.getPhone() != null) {
                Phone newPhone = user.getPhone();
                Phone oldPhone = editUser.getPhone();
                if (oldPhone != null) { // if user had phone before
                    oldPhone.setData(newPhone);
                    phoneService.savePhone(oldPhone);
                } else { // if there was no phone
                    editUser.setPhone(phoneService.savePhone(user.getPhone()));
                }
            }
            editUser = userService.updateUser(editUser);
            return new ResponseEntity<>(editUser, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* ORDERS calls */

    @GetMapping("/orders/{username}")
    public ResponseEntity<List<JSONObject>> userOrders(
            @RequestHeader(value = "auth") String basicAuthToken,
            @PathVariable(value = "username") String username
    ) {
        try {
            var user = userService.getUserByBasicAuth(username, basicAuthToken);
            List<Order> orders = orderService.getOrdersByUser(user);
            List<JSONObject> resJson = new ArrayList<>();
            for (Order order : orders) {
                JSONObject tmp = new JSONObject();
                tmp.put("price", order.getPrice());
                tmp.put("ordered_at", order.getOrderedAt());
                tmp.put("delivered_at", order.getDeliveredAt());
                List<String> items = new ArrayList<>();
                for (OrderItem orderItem : order.getOrderItems()) {
                    items.add(orderItem.getItem().getName());
                }
                tmp.put("items", items);
                resJson.add(tmp);
            }
            return new ResponseEntity<>(resJson, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<JSONObject> addOrder(@RequestParam List<Long> mealsId, @RequestParam Long userId) {
        try {
            User user = userService.getUserById(userId);
            List<Item> orderItems = new ArrayList<>();
            for (Long id : mealsId) {
                orderItems.add(itemService.getItemById(id));
            }
            Order order = new Order();
            order.setUser(user);
            Integer price = 0;
            for (Item item : orderItems) {
                price += item.getPrice();
            }
            order.setPrice(price);
            Order result = orderService.saveOrder(order);
            for (Item item : orderItems) {
                orderItemService.saveOrderItem(new OrderItem().setDependencies(item, result));
            }

            JSONObject jo = new JSONObject();
            jo.put("price", result.getPrice());
            jo.put("user", result.getUser().getUsername());
            List<String> itemsNames = new ArrayList<>();
            for (Item item : orderItems) {
                itemsNames.add(item.getName());
            }
            jo.put("order_content", itemsNames);

            return new ResponseEntity<>(jo, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable(value = "id") Long orderID) {
        try {
            orderService.deleteOrder(orderID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ITEMS calls */

    @GetMapping("/items/{restaurant_id}")
    public ResponseEntity<List<JSONObject>> getItemsByRestaurant(@PathVariable(value = "restaurant_id") Long restaurantID)
            throws ResourceNotFoundException {
        try {

            List<Item> result = itemService.findByRestaurId(restaurantID);
            List<JSONObject> resJson = new ArrayList<>();
            for (Item item : result) {
                JSONObject tmp = new JSONObject();
                tmp.put("id", item.getId());
                tmp.put("price", item.getPrice());
                tmp.put("description", item.getDescription());
                tmp.put("name", item.getName());
                tmp.put("restaurant_name", item.getRestaurant().getName());
                resJson.add(tmp);
            }
            return new ResponseEntity<>(resJson, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<JSONObject> addProduct(@RequestBody Item item, @RequestParam Long restaurantID){
        try {
            item.setRestaurant(restaurantService.getRestaurantById(restaurantID));
            itemService.saveItem(item);

            JSONObject jo = new JSONObject();
            jo.put("id", item.getId());
            jo.put("price", item.getPrice());
            jo.put("description", item.getDescription());
            jo.put("name", item.getName());
            jo.put("restaurant_name", item.getRestaurant().getName());

            return new ResponseEntity<>(jo, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable(value = "id") Long itemID) {
        try {
            itemService.deleteItemById(itemID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }  catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<JSONObject> editItem(@RequestBody Item item, @PathVariable(value = "id") Long itemID) {
        try {
            Item editItem = itemService.getItemById(itemID);
            if (item.getName() != null) {
                editItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                editItem.setDescription(item.getDescription());
            }
            if (item.getPrice() != null) {
                editItem.setPrice(item.getPrice());
            }
            if (item.getPhoto() != null) {
                Photo newPhoto = item.getPhoto();
                Photo oldPhoto = item.getPhoto();

                if (oldPhoto != null) { // if user had phone before
                    oldPhoto.setPath(newPhoto.getPath());
                    photoService.savePhoto(oldPhoto);
                } else { // if there was no phone
                    editItem.setPhoto(photoService.savePhoto(newPhoto));
                }
            }
            editItem = itemService.saveItem(editItem);

            JSONObject jo = new JSONObject();
            jo.put("price", editItem.getPrice());
            jo.put("description", editItem.getDescription());
            jo.put("name", editItem.getName());
            jo.put("restaurant_name", editItem.getRestaurant().getName());

            return new ResponseEntity<>(jo, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* RESTAURANTS calls */

    @GetMapping("/restaurants")
    public ResponseEntity<List<JSONObject>> filterRestaurants(@RequestBody (required = false) JSONObject filters) {
        try {
            List<Restaurant> result = filters == null
                    ? restaurantService.findAllRestaurants()
                    : restaurantService.filterRestaurants(filters);
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
            return new ResponseEntity<>(resJson, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant, @RequestParam Long managerId) {
        try {
            restaurant.setManager(userService.getUserById(managerId));
            phoneService.savePhone(restaurant.getPhone());
            addressService.saveAddress(restaurant.getAddress());
            Restaurant res = restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<HttpStatus> deleteRestaurant(@PathVariable(value = "id") Long restaurantID) {
        try {
            restaurantService.deleteRestaurantById(restaurantID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* REVIEWS calls */

    @PostMapping("/restaurants/{id}/reviews")
    public ResponseEntity<Long> addRestaurantReview(
            @PathVariable(value = "id") Long restaurantID,
            @RequestParam String username,
            @RequestHeader(value = "auth") String basicAuthToken,
            @RequestBody Review review
    ) {
        try {
            review.setRestaurant(restaurantService.getRestaurantById(restaurantID));
            var user = userService.getUserByBasicAuth(username, basicAuthToken);
            review.setUser(user);
            var addedReview = reviewService.saveReview(review);
            return new ResponseEntity<>(addedReview.getId(), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reviews/{id}/photos")
    public ResponseEntity<HttpStatus> addReviewPhoto(
            @PathVariable(value = "id") Long reviewID,
            @RequestParam String username,
            @RequestHeader(value = "auth") String basicAuthToken,
            @RequestBody MultipartFile file
    ) {
        try {
            var user = userService.getUserByBasicAuth(username, basicAuthToken);
            var review = reviewService.getByIdAndUser(reviewID, user);
            var filePath = new File(
                    String.valueOf(MEDIA_ROOT),
                    UUID.randomUUID().toString() + file.getOriginalFilename()
            );
            if (!filePath.exists()) {
                if (!filePath.mkdirs()) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            file.transferTo(filePath);
            var photo = new Photo();
            photo.setPath(filePath.toString());
            photoService.savePhoto(photo);
            var reviewPhoto = new ReviewPhoto();
            reviewPhoto.setPhoto(photo);
            reviewPhoto.setReview(review);
            reviewPhotoService.saveReviewPhoto(reviewPhoto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reviews/{review_id}/photos/{photo_id}")
    public ResponseEntity<MultipartFile> getReviewPhoto(
            @PathVariable(value = "review_id") Long reviewID,
            @PathVariable(value = "photo_id") Long photoID
    ) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{id}/reviews")
    public ResponseEntity<List<JSONObject>> getRestaurantReviews(
            @PathVariable(value = "id") Long restaurantID
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/reviews")
    public ResponseEntity<List<JSONObject>> getUserReviews(
            @PathVariable(value = "id") Long userID
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurant_id}/reviews/{review_id}")
    public ResponseEntity<List<JSONObject>> getRestaurantReview(
            @PathVariable(value = "restaurant_id") Long restaurantID,
            @PathVariable(value = "review_id") Long reviewID
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/users/{user_id}/reviews/{review_id}")
    public ResponseEntity<List<JSONObject>> getUserReview(
            @PathVariable(value = "user_id") Long userID,
            @PathVariable(value = "review_id") Long reviewID
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
