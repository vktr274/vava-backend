package sk.vava.zalospevaci.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name="reviews")
@Entity
public class Review {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="score", nullable=false)
    private Integer score;

    @Column(name="text")
    private String text;

    @ManyToOne
    @JoinColumn(name="restaurant_id", nullable=false, referencedColumnName="id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    private User user;

    @CreationTimestamp
    @Column(name="created_at", updatable=false, nullable=false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @UpdateTimestamp
    @Column(name="updated_at", nullable=false)
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
