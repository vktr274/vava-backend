package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name="restaurants")
@Entity
public class Restaurant {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="url")
    private String url;

    @Column(name="blocked", nullable=false)
    private boolean blocked = false;

    @OneToMany(mappedBy="restaurant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="manager_id", nullable=false, referencedColumnName="id")
    private User manager;

    @ManyToOne
    @JoinColumn(name="phone_id", nullable=false, referencedColumnName="id")
    private Phone phone;

    @ManyToOne
    @JoinColumn(name="address_id", nullable=false, referencedColumnName="id")
    private Address address;
}
