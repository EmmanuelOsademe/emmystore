package com.emmydev.ecommerce.client.service.product;

import com.emmydev.ecommerce.client.dto.PageRequestDto;
import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;

import java.util.Optional;

public interface ProductService {

    ResponseDto<Object> saveProduct(ProductDto[] productDto) throws ProductAlreadyExistsException;

    Optional<Product> findProductByName(String productName);

    ResponseDto<Object> fetchProducts(PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchProductsPageList(PageRequestDto pageRequestDto);

    ResponseDto<Object> fetchProductsByCategory(String category, PageRequestDto pageRequestDto);

}
