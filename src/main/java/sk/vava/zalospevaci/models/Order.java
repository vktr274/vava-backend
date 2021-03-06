package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name="note")
    private String note = null;

    @OneToMany(mappedBy="order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false, referencedColumnName="id")
    private User user;

    @CreationTimestamp
    @Column(name="ordered_at", updatable=false, nullable=false)
    @JsonProperty("ordered_at")
    private Timestamp orderedAt = new Timestamp(System.currentTimeMillis());
}
