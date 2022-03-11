package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="orders_items")
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="order_id", nullable = false)
    private Integer orderId;

    @Column(name="item_id", nullable = false)
    private Integer itemId;

}

