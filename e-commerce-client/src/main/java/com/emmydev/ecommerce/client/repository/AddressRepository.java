package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
