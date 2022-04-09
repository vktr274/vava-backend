package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Review;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.ReviewRepository;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getById(Long id) throws NotFoundException {
        var review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return review;
    }
    public Review getByIdAndUser(Long id, User user) throws NotFoundException {
        var review = getById(id);
        if (!review.getUser().equals(user)) {
            throw new NotFoundException(user.getUsername() + " has no review with id " + id.toString());
        }
        return review;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
