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

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name="created_at", updatable = false, nullable = false)
    protected Timestamp createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable = false)
    protected Timestamp updatedAt;
}
