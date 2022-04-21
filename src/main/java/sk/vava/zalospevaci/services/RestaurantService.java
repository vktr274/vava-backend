package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Restaurant;
import sk.vava.zalospevaci.repositories.RestaurantRepository;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    public Restaurant getRestaurantById(Long id) throws NotFoundException {
        var restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return restaurant;
    }

    public Page<Restaurant> getByAll(String namePart, String city, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findByAddressAndBlockedAndName(city, blocked, namePart,
                pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with '" + namePart + "' in their name and " + city + " in their city name and " +
                            "status blocked = " + blocked + " not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByNameAndCity(String namePart, String city, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findByAddressAndName(city, namePart,
                pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with '" + namePart + "' in their name and '" + city + "' in their city name not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByCityAndBlocked( String city, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findByAddressAndBlocked(city, blocked,
                pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with '" + city + "' in their city name and status blocked = " + blocked + " not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByNameAndBlocked(String namePart, Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findAllByNameContainingAndBlocked(namePart, blocked,
                pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with '" + namePart + "' in their name and status blocked = " + blocked + " not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByName(String namePart, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findByNameContaining(namePart, pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with '" + namePart + "' in their name not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByStatus(Boolean blocked, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findAllByBlocked(blocked, pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "restaurants with status blocked = '" + blocked + "' not found"
            );
        }
        return restaurants;
    }

    public Page<Restaurant> getByCity(String city, Pageable pageable)
            throws NotFoundException
    {
        var restaurants = restaurantRepository.findByAddress(city, pageable).orElse(null);
        if (restaurants == null) {
            throw new NotFoundException(
                    "addresses with '" + city + "' in their city name not found"
            );
        }
        return restaurants;
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
    }
}
