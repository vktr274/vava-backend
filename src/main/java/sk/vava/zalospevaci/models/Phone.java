package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = "^[+][0-9]{3}$", message = "Incorrect country code format")
    private String countryCode;

    @Column(name="number", nullable=false)
    @Pattern(regexp = "^[0-9]{9}$", message = "Incorrect phone number format")
    private String number;
}
