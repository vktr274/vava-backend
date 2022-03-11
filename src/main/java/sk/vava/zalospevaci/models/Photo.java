package sk.vava.zalospevaci.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="photos")
@Entity
public class Photo {
    @Id
    @GeneratedValue
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="encoded", nullable = false)
    private String username;
}
