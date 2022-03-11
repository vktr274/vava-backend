package sk.vava.zalospevaci.models;

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
    private Timestamp orderedAt = new Timestamp(System.currentTimeMillis());

    @UpdateTimestamp
    @Column(name="delivered_at")
    private Timestamp deliveredAt = null;
}
