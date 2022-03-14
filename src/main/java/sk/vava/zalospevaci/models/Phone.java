package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="phones")
@Entity
public class Phone {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="country_code", nullable=false)
    @JsonProperty("country_code")
    private String countryCode;

    @Column(name="number", nullable=false)
    private String number;
}
