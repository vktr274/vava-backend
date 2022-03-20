package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

//    @Filter(name = "usernameFilter", condition = "username ILIKE :username%")
    @Column(name="username", unique=true, nullable=false)
    private String username;

    @Column(name="email", unique=true, nullable=false)
    private String email;

    @Column(name="password", nullable=false)
    private String password;

    @Column(name="role", nullable=false)
    private String role;

    @Column(name="blocked", nullable=false)
    private boolean blocked = false;

    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="phone_id", referencedColumnName="id")
    private Phone phone = null;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="address_id", referencedColumnName="id")
    private Address address = null;
}
