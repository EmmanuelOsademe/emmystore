package com.emmydev.ecommerce.client.service.product;

import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface ProductService {

    ResponseDto<Object> saveProduct(ProductDto[] productDto) throws ProductAlreadyExistsException;

    Optional<Product> findProductByName(String productName);

    ResponseDto<Object> fetchProducts(int pageNumber);
}
