package sk.vava.zalospevaci.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name="reviews")
@Entity
public class Review {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="score", nullable = false)
    private Integer score;

    @Column(name="text")
    private String text;

    @Column(name="restaurant_id", nullable = false)
    private Integer restaurantId;

    @Column(name="user_id", nullable = false)
    private Integer userId;

    @CreationTimestamp
    @Column(name="created_at", updatable = false, nullable = false)
    protected Timestamp createdAt = new Timestamp(System.currentTimeMillis());

}

