package sk.vava.zalospevaci.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name="reviews_photos")
@Entity
public class ReviewPhoto {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="photo_id", nullable = false)
    private Integer photoId;

    @Column(name="review_id", nullable = false)
    private Integer reviewId;

}

