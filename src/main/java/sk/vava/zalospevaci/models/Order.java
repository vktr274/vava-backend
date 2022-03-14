package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name="orders")
@Entity
public class Order {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="price", nullable=false)
    private Integer price;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    private User user;

    @CreationTimestamp
    @Column(name="ordered_at", updatable=false, nullable=false)
    @JsonProperty("ordered_at")
    private Timestamp orderedAt = new Timestamp(System.currentTimeMillis());

    @UpdateTimestamp
    @Column(name="delivered_at")
    @JsonProperty("delivered_at")
    private Timestamp deliveredAt = null;
}
