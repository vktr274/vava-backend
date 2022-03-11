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

    @ManyToOne
    @JoinColumn(name="photo_id", nullable = false)
    private Photo photo;

    @ManyToOne
    @JoinColumn(name="review_id", nullable = false)
    private Review review;
}
