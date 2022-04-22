package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="reviews_photos")
@Entity
public class ReviewPhoto {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="photo_id", nullable=false, referencedColumnName="id")
    private Photo photo;

    @ManyToOne
    @JoinColumn(name="review_id", nullable=false, referencedColumnName="id")
    private Review review;
}
