package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="orders_items")
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false, referencedColumnName="id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="item_id", nullable=false, referencedColumnName="id")
    private Item item;
}
