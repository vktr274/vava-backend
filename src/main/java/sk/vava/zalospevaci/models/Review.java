package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name="reviews")
@Entity
public class Review {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="score", nullable=false)
    @Min(value = 1, message = "Incorrect score, [1, 10] only")
    @Max(value = 10, message = "Incorrect score, [1, 10] only")
    private Integer score;

    @Column(name="text")
    private String text;

    @OneToMany(mappedBy="review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="restaurant_id", nullable=false, referencedColumnName="id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    private User user;

    @CreationTimestamp
    @Column(name="created_at", updatable=false, nullable=false)
    @JsonProperty("created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
