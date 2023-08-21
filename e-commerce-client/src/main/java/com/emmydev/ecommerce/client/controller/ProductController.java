package com.emmydev.ecommerce.client.controller;

import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;
import com.emmydev.ecommerce.client.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("new-products")
    public ResponseEntity<ResponseDto<Object>> saveProducts(@Valid @RequestBody ProductDto[] products) throws ProductAlreadyExistsException {
        return ResponseEntity.ok(productService.saveProduct(products));
    }
}
