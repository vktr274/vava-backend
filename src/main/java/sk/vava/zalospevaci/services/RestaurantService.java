package sk.vava.zalospevaci.services;

import net.minidev.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.artifacts.HibernateUtil;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Address;
import sk.vava.zalospevaci.models.Restaurant;
import sk.vava.zalospevaci.repositories.RestaurantRepository;

import javax.persistence.criteria.*;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(Long id) throws NotFoundException {
        var restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return restaurant;
    }

    public List<Restaurant> filterRestaurants(JSONObject obj) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        // Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Restaurant> criteria = builder.createQuery(Restaurant.class);
        Root<Restaurant> root = criteria.from(Restaurant.class);

        Predicate namePred = builder.and();
        Predicate idPred = builder.and();
        Predicate addrCityPred = builder.and();
        Predicate scorePred = builder.and(); // currently unused

        if (obj.containsKey("id")) {
            idPred = builder.equal(root.get("id"), obj.getAsNumber("id"));
        }

        if (obj.containsKey("name")) {
            namePred = builder.like(root.get("name"), obj.getAsString("name")+"%");
        }

        /* Template for a score filter */
       /* if (obj.containsKey("score")) {
            // Retrieve number array from JSON object.
            JSONArray array = (JSONArray)obj.get("score");

            int minScore = (int)array.get(0);
            int maxScore = (int)array.get(1);

            scorePred = builder.between(root.get("score"), minScore, maxScore);
        }*/

        Join<Restaurant, Address> userAddrJoin = null;

        if (obj.containsKey("city")) {
            userAddrJoin = root.join("address");
            addrCityPred = builder.like(userAddrJoin.get("city"), obj.getAsString("city"));
        }

        criteria.where(builder.and(idPred, namePred, scorePred, addrCityPred));

        Query<Restaurant> query = session.createQuery(criteria);

        return query.getResultList();
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
    }
}
