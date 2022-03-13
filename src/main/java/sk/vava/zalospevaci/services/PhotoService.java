package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.models.Photo;
import sk.vava.zalospevaci.repositories.PhotoRepository;

import java.util.List;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public List<Photo> findAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }
}
