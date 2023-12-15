package io.nuvalence.workmanager.service.domain.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2", nullable = false)
    private String address2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "county", nullable = false)
    private String county;

    @OneToOne
    @JoinColumn(name = "employer_for_mailing_id", referencedColumnName = "id")
    private Employer employerForMailing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_for_locations_id", referencedColumnName = "id")
    private Employer employerForLocations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equals(id, address.id)
                && Objects.equals(address1, address.address1)
                && Objects.equals(address2, address.address2)
                && Objects.equals(city, address.city)
                && Objects.equals(state, address.state)
                && Objects.equals(postalCode, address.postalCode)
                && Objects.equals(country, address.country)
                && Objects.equals(county, address.county)
                && Objects.equals(employerForMailing, address.employerForMailing)
                && Objects.equals(employerForLocations, address.employerForLocations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address1, address2, city, state, postalCode, country, county);
    }
}
