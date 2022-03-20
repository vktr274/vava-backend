package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@Table(name="items")
@Entity
public class Item {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="price", nullable=false)
    @Min(value = 1, message = "Price cannot be lower than 1")
    private Integer price;

    @Column(name="description", nullable=false)
    @Size(max = 10, message = "Too many symbols (255 max)")
    private String description;

    @Column(name="name", nullable=false)
    private String name;

    @ManyToOne
    @JoinColumn(name="photo_id", referencedColumnName="id")
    private Photo photo = null;

    @ManyToOne
    @JoinColumn(name="restaurant_id", nullable=false, referencedColumnName="id")
    private Restaurant restaurant;
}
