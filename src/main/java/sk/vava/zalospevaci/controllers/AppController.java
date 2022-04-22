package sk.vava.zalospevaci.controllers;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.vava.zalospevaci.artifacts.TokenManager;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.*;
import sk.vava.zalospevaci.services.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private JSONObject createUserJson(User user) {
        var userJson = new JSONObject();
        userJson.appendField("email", user.getEmail());
        userJson.appendField("username", user.getUsername());
        userJson.appendField("blocked", user.isBlocked());
        userJson.appendField("id", user.getId());
        userJson.appendField("role", user.getRole());
        userJson.appendField("address", user.getAddress());
        userJson.appendField("phone", user.getPhone());
        return userJson;
    }

    private JSONObject createReviewJson(Review review) throws NotFoundException {
        JSONObject obj = new JSONObject();
        obj.appendField("id", review.getId());
        obj.appendField("username", review.getUser().getUsername());
        obj.appendField("restaurant_id", review.getRestaurant().getId());
        obj.appendField("score", review.getScore());
        obj.appendField("text", review.getText());
        obj.appendField("created_at", review.getCreatedAt());
        var reviewPhotos = reviewPhotoService.getAllByReviewId(review.getId());
        List<Long> photos = new ArrayList<>();
        for (var reviewPhoto : reviewPhotos) {
            photos.add(reviewPhoto.getPhoto().getId());
        }
        obj.appendField("photos", photos);
        return obj;
    }

    private JSONObject createRestaurantJson(Restaurant restaurant) {
        JSONObject tmp = new JSONObject();
        tmp.appendField("name", restaurant.getName());
        tmp.appendField("id", restaurant.getId());
        tmp.appendField("address", restaurant.getAddress());
        tmp.appendField("phone", restaurant.getPhone());
        tmp.appendField("url", restaurant.getUrl());
        tmp.appendField("blocked", restaurant.isBlocked());
        tmp.appendField("rating", restaurant.getRating());
        return tmp;
    }

    private JSONObject createItemJson(Item item) {
        JSONObject jo = new JSONObject();
        jo.put("id", item.getId());
        jo.put("price", item.getPrice());
        jo.put("description", item.getDescription());
        jo.put("name", item.getName());
        jo.put("restaurant_name", item.getRestaurant().getName());
        return jo;
    }

    @PostMapping("/token")
    public ResponseEntity<JSONObject> getToken(
            @RequestBody JSONObject req
    ) {
        try {
            String login = req.getAsString("login");
            String pass = req.getAsString("password");

            User user = userService.getUserByUsername(login);

            if (!Objects.equals(user.getPassword(), pass)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            JSONObject jo = new JSONObject();
            jo.put("role", user.getRole());
            jo.put("token", TokenManager.createToken(user));

            return new ResponseEntity<>(jo, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* USERS calls */

    @GetMapping("/users")
    public ResponseEntity<JSONObject> filterUsers(
            @RequestParam(value = "blocked", required = false) Boolean blocked,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "per_page", required = false) Integer perPage,
            @RequestParam(value= "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestHeader(value = "auth") String authToken
    ) {
        try {
            TokenManager.validToken(authToken, "admin");

            String defSortBy = "id";
            String defSort = "desc";
            int defPage = 0;
            int defPerPage = 10;

            if (page != null) {
                defPage = page;
            }
            if (perPage != null) {
                defPerPage = perPage;
            }
            if (sortBy != null) {
                defSortBy = sortBy;
            }
            if (sort != null) {
                defSort = sort;
            }

            Page<User> users;
            Sort sortObj = null;

            if (defSort.equalsIgnoreCase("asc")) {
                sortObj = Sort.by(defSortBy).ascending();
            } else if (defSort.equalsIgnoreCase("desc")) {
                sortObj = Sort.by(defSortBy).descending();
            }
            Pageable pageable = sortObj == null ? PageRequest.of(defPage, defPerPage) : PageRequest.of(defPage, defPerPage, sortObj);
            if (blocked != null && role != null && name != null) {
                users = userService.getByAll(name, role, blocked, pageable);
            } else if (role != null && name != null) {
                users = userService.getByNameAndRole(name, role, pageable);
            } else if (role != null && blocked != null) {
                users = userService.getByRoleAndBlocked(role, blocked, pageable);
            } else if (name != null && blocked != null) {
                users = userService.getByNameAndBlocked(name, blocked, pageable);
            } else if (blocked != null) {
                users = userService.getByStatus(blocked, pageable);
            } else if (role != null) {
                users = userService.getByRole(role, pageable);
            } else if (name != null) {
                users = userService.getByName(name, pageable);
            } else {
                users = userService.getAllUsers(pageable);
            }

            JSONObject finalJson = new JSONObject();
            List<JSONObject> usersJson = new ArrayList<>();
            for (var user : users) {
                usersJson.add(createUserJson(user));
            }
            JSONObject metadata = new JSONObject();
            metadata.appendField("page", defPage);
            metadata.appendField("per_page", defPerPage);
            metadata.appendField("sort", defSort);
            metadata.appendField("sort_by", defSortBy);
            metadata.appendField("total_pages", users.getTotalPages());
            metadata.appendField("total_elements", users.getTotalElements());

            finalJson.appendField("users", usersJson);
            finalJson.appendField("metadata", metadata);

            return new ResponseEntity<>(finalJson, HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{user_id}/state")
    public ResponseEntity<Restaurant> setUserState(
            @PathVariable (value="user_id") Long userId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "admin");
            var user = userService.getUserById(userId);
            user.setBlocked(!user.isBlocked());
            userService.updateUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
    * GET method to get info about user with {username} specified
    * */
    @GetMapping("/users/{username}")
    public ResponseEntity<JSONObject> getUserByLogin(
            @PathVariable(value = "username") String username
    ) {
        try {
            User user = userService.getUserByUsername(username);
            return new ResponseEntity<>(createUserJson(user), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<JSONObject> registerUser(
            @RequestBody User user
    ) {
        try {
            if (user.getAddress() != null) {
                addressService.saveAddress(user.getAddress());
            }
            if (user.getPhone() != null) {
                phoneService.savePhone(user.getPhone());
            }
            user = userService.saveUser(user);
            return new ResponseEntity<>(createUserJson(user), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> delUser(
            @PathVariable(value = "id") Long userId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            var delUser = userService.getUserById(userId);
            if (delUser.equals(user) || user.getRole().equals("admin")) {
                userService.delUser(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<JSONObject> editUser(
            @RequestBody User user,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            User editUser = userService.getUserById(TokenManager.getIdByToken(token));
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
            return new ResponseEntity<>(createUserJson(editUser), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* ORDERS calls */

    @GetMapping("/orders")
    public ResponseEntity<List<JSONObject>> userOrders(
            @RequestHeader(value = "auth") String token
    ) {
        try {
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            List<Order> orders = orderService.getOrdersByUser(user);
            List<JSONObject> resJson = new ArrayList<>();
            for (Order order : orders) {
                JSONObject tmp = new JSONObject();
                tmp.put("price", order.getPrice());
                tmp.put("note", order.getNote());
                tmp.put("ordered_at", order.getOrderedAt());
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<JSONObject> addOrder(
            @RequestParam List<Long> mealsId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            User user = userService.getUserById(TokenManager.getIdByToken(token));
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
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(
            @PathVariable(value = "id") Long orderId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "admin");
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ITEMS calls */

    @GetMapping("/items/{restaurant_id}")
    public ResponseEntity<List<JSONObject>> getItemsByRestaurant(
            @PathVariable(value = "restaurant_id") Long restaurantId
    ) {
        try {
            List<Item> result = itemService.getByRestaurantId(restaurantId);
            List<JSONObject> resJson = new ArrayList<>();
            for (Item item : result) {
                resJson.add(createItemJson(item));
            }
            return new ResponseEntity<>(resJson, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<JSONObject> addProduct(
            @RequestBody Item item, @RequestParam Long restaurantId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "manager");
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            var restaurant = restaurantService.getRestaurantById(restaurantId);
            if (restaurant.getManager() != user) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            item.setRestaurant(restaurant);
            itemService.saveItem(item);

            return new ResponseEntity<>(createItemJson(item), HttpStatus.CREATED);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(
            @PathVariable(value = "id") Long itemId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "manager");
            var item = itemService.getItemById(itemId);
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            if (item.getRestaurant().getManager() != user) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            itemService.deleteItemById(itemId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<JSONObject> editItem(
            @RequestBody Item item,
            @PathVariable(value = "id") Long itemId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            Item editItem = itemService.getItemById(itemId);
            TokenManager.validToken(token, "manager");
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            if (editItem.getRestaurant().getManager() != user) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
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
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* RESTAURANTS calls */

    @GetMapping("/restaurants")
    public ResponseEntity<JSONObject> filterRestaurants(
            @RequestParam(value = "blocked", required = false) Boolean blocked,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "per_page", required = false) Integer perPage,
            @RequestParam(value= "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        try {
            String defSortBy = "id";
            String defSort = "desc";
            int defPage = 0;
            int defPerPage = 10;

            if (page != null) {
                defPage = page;
            }
            if (perPage != null) {
                defPerPage = perPage;
            }
            if (sortBy != null) {
                defSortBy = sortBy;
            }
            if (sort != null) {
                defSort = sort;
            }

            Page<Restaurant> restaurants;
            Sort sortObj = null;

            if (defSort.equalsIgnoreCase("asc")) {
                sortObj = Sort.by(defSortBy).ascending();
            } else if (defSort.equalsIgnoreCase("desc")) {
                sortObj = Sort.by(defSortBy).descending();
            }
            Pageable pageable = sortObj == null ? PageRequest.of(defPage, defPerPage) : PageRequest.of(defPage, defPerPage, sortObj);
            if (name != null && city != null && blocked != null) {
                restaurants = restaurantService.getByAll(name, city, blocked, pageable);
            } else if (name != null && blocked != null) {
                restaurants = restaurantService.getByNameAndBlocked(name, blocked, pageable);
            } else if (name != null && city != null) {
                restaurants = restaurantService.getByNameAndCity(name, city, pageable);
            } else if (city != null && blocked != null) {
                restaurants = restaurantService.getByCityAndBlocked(city, blocked, pageable);
            } else if (blocked != null) {
                restaurants = restaurantService.getByStatus(blocked, pageable);
            } else if (name != null) {
                restaurants = restaurantService.getByName(name, pageable);
            } else if (city != null) {
                restaurants = restaurantService.getByCity(city, pageable);
            } else {
                restaurants = restaurantService.getAllRestaurants(pageable);
            }

            JSONObject finalJson = new JSONObject();
            List<JSONObject> restaurantsJson = new ArrayList<>();
            for (var rest : restaurants) {
                restaurantsJson.add(createRestaurantJson(rest));
            }
            JSONObject metadata = new JSONObject();
            metadata.appendField("page", defPage);
            metadata.appendField("per_page", defPerPage);
            metadata.appendField("sort", defSort);
            metadata.appendField("sort_by", defSortBy);
            metadata.appendField("total_pages", restaurants.getTotalPages());
            metadata.appendField("total_elements", restaurants.getTotalElements());

            finalJson.appendField("restaurants", restaurantsJson);
            finalJson.appendField("metadata", metadata);

            return new ResponseEntity<>(finalJson, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/restaurants/manage")
    public ResponseEntity<JSONObject> filterRestaurantsManager(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "per_page", required = false) Integer perPage,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "manager");
            String defSortBy = "id";
            String defSort = "desc";
            int defPage = 0;
            int defPerPage = 10;

            if (page != null) {
                defPage = page;
            }
            if (perPage != null) {
                defPerPage = perPage;
            }

            Page<Restaurant> restaurants;
            Sort sortObj = null;
            sortObj = Sort.by(defSortBy).ascending();

            Pageable pageable = PageRequest.of(defPage, defPerPage, sortObj);

            var manager = userService.getUserById(TokenManager.getIdByToken(token));

            restaurants = restaurantService.getByManager(manager, pageable);

            JSONObject finalJson = new JSONObject();
            List<JSONObject> restaurantsJson = new ArrayList<>();
            for (var rest : restaurants) {
                restaurantsJson.add(createRestaurantJson(rest));
            }
            JSONObject metadata = new JSONObject();
            metadata.appendField("page", defPage);
            metadata.appendField("per_page", defPerPage);
            metadata.appendField("sort", defSort);
            metadata.appendField("sort_by", defSortBy);
            metadata.appendField("total_pages", restaurants.getTotalPages());
            metadata.appendField("total_elements", restaurants.getTotalElements());

            finalJson.appendField("restaurants", restaurantsJson);
            finalJson.appendField("metadata", metadata);

            return new ResponseEntity<>(finalJson, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<Restaurant> addRestaurant(
            @RequestBody Restaurant restaurant,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "manager");
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            restaurant.setManager(user);
            phoneService.savePhone(restaurant.getPhone());
            addressService.saveAddress(restaurant.getAddress());
            Restaurant res = restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/restaurants/{restaurant_id}/state")
    public ResponseEntity<Restaurant> setRestaurantState(
            @PathVariable (value="restaurant_id") Long restaurantId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "admin");
            var restaurant = restaurantService.getRestaurantById(restaurantId);
            restaurant.setBlocked(!restaurant.isBlocked());
            restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotAuthorizedException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<HttpStatus> deleteRestaurant(
            @PathVariable(value = "id") Long restaurantId,
            @RequestHeader(value = "auth") String token
    ) {
        try {
            TokenManager.validToken(token, "manager");
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            var restaurant = restaurantService.getRestaurantById(restaurantId);
            if (restaurant.getManager() != user && !user.getRole().equals("admin")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            restaurantService.deleteRestaurantById(restaurantId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* REVIEWS calls */

    @PostMapping("/reviews")
    public ResponseEntity<Long> addRestaurantReview(
            @RequestParam(value = "restaurant_id") Long restaurantId,
            @RequestHeader(value = "auth") String token,
            @RequestBody Review review
    ) {
        try {
            review.setRestaurant(restaurantService.getRestaurantById(restaurantId));
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            review.setUser(user);
            var addedReview = reviewService.saveReview(review);
            return new ResponseEntity<>(addedReview.getId(), HttpStatus.CREATED);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/photos")
    public ResponseEntity<HttpStatus> addReviewPhoto(
            @RequestParam(value = "review_id") Long reviewId,
            @RequestHeader(value = "auth") String token,
            @RequestBody MultipartFile file
    ) {
        try {
            var user = userService.getUserById(TokenManager.getIdByToken(token));
            var review = reviewService.getByIdAndUser(reviewId, user);
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
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/photos/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(
            @PathVariable(value = "id") Long photoId
    ) {
        try {
            var photoData = new FileInputStream(photoService.getById(photoId).getPath());
            return new ResponseEntity<>(photoData.readAllBytes(), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<JSONObject> getReviews(
            @RequestParam(value = "restaurant_id", required = false) Long restaurantId,
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "per_page", required = false) Integer perPage,
            @RequestParam(value= "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        try {
            String defSortBy = "id";
            String defSort = "desc";
            int defPage = 0;
            int defPerPage = 10;

            if (page != null) {
                defPage = page;
            }
            if (perPage != null) {
                defPerPage = perPage;
            }
            if (sortBy != null) {
                defSortBy = sortBy;
            }
            if (sort != null) {
                defSort = sort;
            }

            Page<Review> reviews;
            Sort sortObj = null;
            if (defSort.equalsIgnoreCase("asc")) {
                sortObj = Sort.by(defSortBy).ascending();
            } else if (defSort.equalsIgnoreCase("desc")) {
                sortObj = Sort.by(defSortBy).descending();
            }
            Pageable pageable = sortObj == null ? PageRequest.of(defPage, defPerPage) : PageRequest.of(defPage, defPerPage, sortObj);
            if (restaurantId != null && userId != null) {
                reviews = reviewService.getByRestaurantAndUser(
                        restaurantService.getRestaurantById(restaurantId),
                        userService.getUserById(userId),
                        pageable
                );
            } else if (restaurantId != null) {
                reviews = reviewService.getByRestaurant(
                        restaurantService.getRestaurantById(restaurantId),
                        pageable
                );
            } else if (userId != null) {
                reviews = reviewService.getByUser(
                        userService.getUserById(userId),
                        pageable
                );
            } else {
                reviews = reviewService.getAllReviews(
                        pageable
                );
            }
            JSONObject finalJson = new JSONObject();
            List<JSONObject> reviewsJson = new ArrayList<>();
            for (var review : reviews) {
                reviewsJson.add(createReviewJson(review));
            }
            JSONObject metadata = new JSONObject();
            metadata.appendField("page", defPage);
            metadata.appendField("per_page", defPerPage);
            metadata.appendField("sort", defSort);
            metadata.appendField("sort_by", defSortBy);
            metadata.appendField("total_pages", reviews.getTotalPages());
            metadata.appendField("total_elements", reviews.getTotalElements());

            finalJson.appendField("reviews", reviewsJson);
            finalJson.appendField("metadata", metadata);

            return new ResponseEntity<>(finalJson, HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<JSONObject> getReview(
            @PathVariable(value = "id") Long reviewID
    ) {
        try {
            var review = reviewService.getById(reviewID);
            return new ResponseEntity<>(createReviewJson(review), HttpStatus.OK);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<HttpStatus> deleteReview(
            @PathVariable(value = "id") Long reviewID
    ) {
        try {
            var review = reviewService.getById(reviewID);
            reviewService.deleteById(review);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}