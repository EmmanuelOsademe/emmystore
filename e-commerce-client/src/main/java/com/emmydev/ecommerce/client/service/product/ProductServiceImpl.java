package com.emmydev.ecommerce.client.service.product;

import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.enums.Manufacturer;
import com.emmydev.ecommerce.client.enums.ProductCategory;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;
import com.emmydev.ecommerce.client.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ResponseDto<Object> saveProduct(ProductDto[] productDtos) throws ProductAlreadyExistsException {
        // Sanitise the data to be sure there are no duplicates
        List<Product> products = processNewProducts(productDtos);

        // Save sanitised data
        for(Product product: products){
            productRepository.save(product);
        }

        // Return response DTO
        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Products successfully created")
                .data(products)
                .build();
    }

    @Override
    public Optional<Product> findProductByName(String productName) {
        Optional<Product> dbProduct = productRepository.findByName(productName);
        return dbProduct;
    }

    private List<Product> processNewProducts(ProductDto[] productDtos) throws ProductAlreadyExistsException {
        // Store valid products
        List<Product> processedProducts = new ArrayList<>();

        // Store invalidProducts
        List<ProductDto> existingProducts = new ArrayList<>();

        for(ProductDto productDto: productDtos){
            Optional<Product> dbProduct = findProductByName(productDto.getName());
            if(dbProduct.isPresent()){
                // If the product already exists, add it to the invalidProducts list
                existingProducts.add(productDto);
            }else{
                // Create a product from the product dto
                Product newProduct = new Product();
                newProduct.setName(productDto.getName());
                newProduct.setDescription(productDto.getDescription());
                newProduct.setPrice(productDto.getPrice());
                newProduct.setImage(productDto.getImage());
                newProduct.setDiscountRate(productDto.getDiscountRate());
                newProduct.setAvailableQuantity(productDto.getAvailableQuantity());
                newProduct.setMinimumQuantity(productDto.getMinimumQuantity());
                newProduct.setFeatured(productDto.isFeatured());
                newProduct.setFreeShipping(productDto.isFreeShipping());
                newProduct.setProductCategory(matchCategory(productDto.getProductCategory().toLowerCase()));
                newProduct.setManufacturer(matchManufacturer(productDto.getManufacturer().toLowerCase()));
                newProduct.setStoreLocation(productDto.getStoreLocation());

                // Add this to the processed products list
                processedProducts.add(newProduct);
            }
        }

        // Throw an error if one of the products already exist
        if(existingProducts.size() != 0){
            throw new ProductAlreadyExistsException(existingProducts.toString());
        }
        return processedProducts;
    }

    private Manufacturer matchManufacturer(String productManufacturer) {
        Manufacturer manufacturer = null;
        switch (productManufacturer){
            case "lidl":
                manufacturer = Manufacturer.LIDL;
                break;
            case "marcos":
                manufacturer = Manufacturer.MARCOS;
                break;
            case "argos":
                manufacturer = Manufacturer.ARGOS;
                break;
            case "nike":
                manufacturer = Manufacturer.NIKE;
                break;
            case "adidas":
                manufacturer = Manufacturer.ADIDAS;
                break;
        }
        return manufacturer;
    }

    private ProductCategory matchCategory(String productCategory) {
        ProductCategory category = null;

        switch (productCategory){
            case "electronics":
                category = ProductCategory.ELECTRONICS;
                break;
            case "fashion":
                category = ProductCategory.FASHION;
                break;
            case "kitchen":
                category = ProductCategory.KITCHEN;
                break;
            case "computing":
                category = ProductCategory.COMPUTING;
                break;
            case "home":
                category = ProductCategory.HOME;
                break;
            case "office":
                category = ProductCategory.OFFICE;
                break;
            case "general":
                category = ProductCategory.GENERAL;
                break;
        }
        return category;
    }
}
