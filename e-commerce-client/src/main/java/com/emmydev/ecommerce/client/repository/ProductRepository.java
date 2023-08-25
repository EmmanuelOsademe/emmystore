package com.emmydev.ecommerce.client.repository;

import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.enums.Manufacturer;
import com.emmydev.ecommerce.client.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository <Product, Long> {

    Optional<Product> findByNameIgnoreCase(String productName);

    Optional<Product> findById(Long productId);

    Page<Product> findByProductCategory(ProductCategory category, Pageable pageable);

    // exists by Id
    // CrudRepository, ListCrudRepository, PagingAndSortingRepository
    // List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);
    //Streamable<Person> result = repository.findByFirstnameContaining("av").and(repository.findByLastnameContaining("ea"));

}
