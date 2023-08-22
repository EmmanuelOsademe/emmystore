package com.emmydev.ecommerce.client.controller;

import com.emmydev.ecommerce.client.dto.PageRequestDto;
import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ProductUpdateDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;
import com.emmydev.ecommerce.client.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    public ResponseEntity<ResponseDto<Object>> updateProducts(@RequestBody ProductUpdateDto[] products){
        return ResponseEntity.ok(productService.updateProducts(products));
    }

    @PostMapping("products")
    public ResponseEntity<ResponseDto<Object>> fetchProducts(@RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(productService.fetchProducts(pageRequestDto));
    }

    @PostMapping("list")
    public ResponseEntity<ResponseDto<Object>> fetchProductsList(@RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(productService.fetchProductsPageList(pageRequestDto));
    }

    @PostMapping("/category/{category}")
    public ResponseEntity<ResponseDto<Object>> fetchProductsByCategory(@RequestBody PageRequestDto pageRequestDto, @PathVariable(value = "category") String category){
        return ResponseEntity.ok(productService.fetchProductsByCategory(category, pageRequestDto));
    }

}
