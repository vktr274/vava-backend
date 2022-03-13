package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.models.ReviewPhoto;
import sk.vava.zalospevaci.repositories.ReviewPhotoRepository;

import java.util.List;

@Service
public class ReviewPhotoService {
    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    public List<ReviewPhoto> findAllReviewPhotos() {
        return reviewPhotoRepository.findAll();
    }

    public ReviewPhoto saveReviewPhoto(ReviewPhoto reviewPhoto) {
        return reviewPhotoRepository.save(reviewPhoto);
    }
}
