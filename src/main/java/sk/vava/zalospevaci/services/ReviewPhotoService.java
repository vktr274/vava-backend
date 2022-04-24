package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.ReviewPhoto;
import sk.vava.zalospevaci.repositories.ReviewPhotoRepository;

import java.util.List;

@Service
public class ReviewPhotoService {
    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    public void saveReviewPhoto(ReviewPhoto reviewPhoto) {
        reviewPhotoRepository.save(reviewPhoto);
    }

    public List<ReviewPhoto> getAllByReviewId(Long reviewID) throws NotFoundException {
        var reviewPhotos = reviewPhotoRepository.findAllByReviewId(reviewID).orElse(null);
        if (reviewPhotos == null) {
            throw new NotFoundException("photos for " + reviewID.toString() + " not found");
        }
        return reviewPhotos;
    }
}
