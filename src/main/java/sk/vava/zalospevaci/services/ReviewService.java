package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Restaurant;
import sk.vava.zalospevaci.models.Review;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.ReviewRepository;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public Review getById(Long id) throws NotFoundException {
        var review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return review;
    }

    public Review getByIdAndUser(Long id, User user) throws NotFoundException {
        var review = reviewRepository.findByIdAndUser(id, user).orElse(null);
        if (review == null) {
            throw new NotFoundException(user.getUsername() + " has no review with id " + id.toString());
        }
        return review;
    }

    public Page<Review> getByRestaurantAndUser(Restaurant restaurant, User user, Pageable pageable)
            throws NotFoundException
    {
        var reviews = reviewRepository.findAllByRestaurantAndUser(restaurant, user, pageable).orElse(null);
        if (reviews == null) {
            throw new NotFoundException(
                    "review for " + restaurant.getName() + " and " + user.getUsername() + " not found"
            );
        }
        return reviews;
    }

    public Page<Review> getByRestaurant(Restaurant restaurant, Pageable pageable) throws NotFoundException {
        var reviews = reviewRepository.findAllByRestaurant(restaurant, pageable).orElse(null);
        if (reviews == null) {
            throw new NotFoundException(
                    "review for " + restaurant.getName() + " not found"
            );
        }
        return reviews;
    }

    public Page<Review> getByUser(User user, Pageable pageable) throws NotFoundException {
        var reviews = reviewRepository.findAllByUser(user, pageable).orElse(null);
        if (reviews == null) {
            throw new NotFoundException(
                    "review for " + user.getUsername() + " not found"
            );
        }
        return reviews;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
