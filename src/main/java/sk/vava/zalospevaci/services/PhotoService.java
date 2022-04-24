package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Photo;
import sk.vava.zalospevaci.repositories.PhotoRepository;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public Photo getById(Long id) throws NotFoundException {
        var photo = photoRepository.findById(id).orElse(null);
        if (photo == null) {
            throw new NotFoundException(id.toString() + " not found");
        }
        return photo;
    }

    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }
}
