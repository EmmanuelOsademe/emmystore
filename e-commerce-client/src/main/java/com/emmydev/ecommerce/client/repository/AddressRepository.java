package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByHouseAddressAndCityAndStateAndCountry(
            String houseAddress,
            String city,
            String state,
            String country
    );
}
