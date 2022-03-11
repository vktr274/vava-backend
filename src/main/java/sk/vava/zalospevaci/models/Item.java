package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="items")
@Entity
public class Item {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="price", nullable = false)
    private Integer price;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="photo_id")
    private Integer photoId = null;

    @Column(name="restaurant_id", nullable = false)
    private Integer restaurantId;

}

