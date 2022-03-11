package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="restaurants")
@Entity
public class Restaurant {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="url")
    private String url;

    @Column(name="blocked", nullable = false)
    private boolean blocked = false;

    @Column(name="manager_id", nullable = false)
    private Integer managerId;

    @Column(name="phone_id", nullable = false)
    private Integer phoneId;

    @Column(name="address_id", nullable = false)
    private Integer addressId;
}

