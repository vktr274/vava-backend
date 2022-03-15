package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="items")
@Entity
public class Item {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="price", nullable=false)
    private Integer price;

    @Column(name="description", nullable=false)
    private String description;

    @Column(name="name", nullable=false)
    private String name;

    @ManyToOne
    @JoinColumn(name="photo_id", referencedColumnName="id")
    private Photo photo = null;

    @ManyToOne
    @JoinColumn(name="restaurant_id", nullable=false, referencedColumnName="id")
    private Restaurant restaurant;

    public Integer getPrice() {
        return price;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
