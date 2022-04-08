package sk.vava.zalospevaci.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name="addresses")
@Entity
public class Address {
    @Id
    @GeneratedValue
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="street")
    private String street = null;

    @Column(name="building_number", nullable=false)
    @JsonProperty("building_number")
    private String buildingNumber;

    @Column(name="city", nullable=false)
    private String city;

    @Column(name="state", nullable=false)
    private String state;

    @Column(name="postcode", nullable=false)
    private String postcode;

    public void setData(Address newAddr) {
        this.name = newAddr.getName();
        this.street = newAddr.getStreet();
        this.buildingNumber = newAddr.getBuildingNumber();
        this.city = newAddr.getCity();
        this.state = newAddr.getState();
        this.postcode = newAddr.getPostcode();
    }
}
