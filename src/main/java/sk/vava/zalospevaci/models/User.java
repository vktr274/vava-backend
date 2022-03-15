package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name="phone_id", referencedColumnName="id")
    private Phone phone = null;

    @ManyToOne
    @JoinColumn(name="address_id", referencedColumnName="id")
    private Address address = null;
}
