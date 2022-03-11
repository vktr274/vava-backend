package sk.vava.zalospevaci.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="username", unique = true, nullable = false)
    private String username;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="role", nullable = false)
    private String role;

    @Column(name="blocked", nullable = false)
    private boolean blocked = false;

    @Column(name="phone_id")
    private Integer phoneId = null;

    @Column(name="address_id")
    private Integer addressId = null;
}

