package com.emmydev.ecommerce.client.service.product;

import com.emmydev.ecommerce.client.config.DefaultProperties;
import com.emmydev.ecommerce.client.dto.PageRequestDto;
import com.emmydev.ecommerce.client.dto.ProductDto;
import com.emmydev.ecommerce.client.dto.ProductUpdateDto;
import com.emmydev.ecommerce.client.dto.ResponseDto;
import com.emmydev.ecommerce.client.entity.Product;
import com.emmydev.ecommerce.client.enums.Manufacturer;
import com.emmydev.ecommerce.client.enums.ProductCategory;
import com.emmydev.ecommerce.client.enums.ResponseCodes;
import com.emmydev.ecommerce.client.exception.ProductAlreadyExistsException;
import com.emmydev.ecommerce.client.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DefaultProperties defaultProperties;

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
    public ResponseDto<Object> updateProducts(ProductUpdateDto[] productUpdateDtos) {
        // Return products to save and products to reject
        Map<String, Object> result = processUpdateData(productUpdateDtos);

        // Return response DTO
        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Products successfully created")
                .data(result)
                .build();
    }


    @Override
    public Optional<Product> findProductByName(String productName) {
        Optional<Product> dbProduct = productRepository.findByName(productName);
        return dbProduct;
    }

    @Override
    public ResponseDto<Object> fetchProducts(PageRequestDto pageRequestDto) {

        Pageable pageable = new  PageRequestDto().getPageable(pageRequestDto);

        Page<Product> products = productRepository.findAll(pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Products successfully fetch")
                .data(products)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchProductsPageList(PageRequestDto pageRequestDto) {

        List<Product> productList = productRepository.findAll();

        // 1. PageListHolder
        PagedListHolder<Product> productPagedListHolder = new PagedListHolder<>(productList);
        productPagedListHolder.setPage(pageRequestDto.getPageNumber());
        productPagedListHolder.setPageSize(pageRequestDto.getPageSize());

        // 2. PropertyComparator
        List<Product> pageSlice = productPagedListHolder.getPageList();
        boolean ascending = pageRequestDto.getSort().isAscending();
        PropertyComparator.sort(pageSlice, new MutableSortDefinition(pageRequestDto.getSortBy(), true, ascending));

        // 3. Page Implementation
        Page<Product> products = new PageImpl<Product>(pageSlice, new PageRequestDto().getPageable(pageRequestDto), productList.size());

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Products successfully fetch")
                .data(products)
                .build();
    }

    @Override
    public ResponseDto<Object> fetchProductsByCategory(String category, PageRequestDto pageRequestDto) {
        // Create pageable
        Pageable pageable = new  PageRequestDto().getPageable(pageRequestDto);
        log.info(category);

        // Make the query
        Page<Product> products = productRepository.findByProductCategory(matchCategory(category), pageable);

        return ResponseDto.builder()
                .responseCode(ResponseCodes.SUCCESS)
                .message("Products successfully fetch")
                .data(products)
                .build();
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

    private Map<String, Object> processUpdateData(ProductUpdateDto[] productUpdateDtos) {
        // Store already existing products
        List<Product> existingProducts = new ArrayList<>();

        // Store nonexistent products
        List<ProductUpdateDto> nonExistentProducts = new ArrayList<>();

        for(ProductUpdateDto productUpdateDto: productUpdateDtos){
            Optional<Product> product = productRepository.findById(productUpdateDto.getId());
            if(product.isPresent()){
                Product dbProduct = product.get();

                // Update the data.
                dbProduct.setName(Objects.nonNull(productUpdateDto.getName()) ? productUpdateDto.getName() : dbProduct.getName());
                dbProduct.setDescription(Objects.nonNull(productUpdateDto.getDescription()) ? productUpdateDto.getDescription() : dbProduct.getDescription());
                dbProduct.setPrice(Objects.nonNull(productUpdateDto.getPrice()) ? productUpdateDto.getPrice() : dbProduct.getPrice());
                dbProduct.setImage(Objects.nonNull(productUpdateDto.getImage()) ? productUpdateDto.getImage() : dbProduct.getImage());
                dbProduct.setDiscountRate(Objects.nonNull(productUpdateDto.getDiscountRate()) ? productUpdateDto.getDiscountRate() : dbProduct.getDiscountRate());
                dbProduct.setAvailableQuantity(Objects.nonNull(productUpdateDto.getAvailableQuantity()) ?
                        (productUpdateDto.getAvailableQuantity() + dbProduct.getAvailableQuantity()) : dbProduct.getAvailableQuantity());
                dbProduct.setMinimumQuantity(Objects.nonNull(productUpdateDto.getMinimumQuantity()) ? productUpdateDto.getMinimumQuantity() : dbProduct.getMinimumQuantity());
                dbProduct.setProductCategory(Objects.nonNull(productUpdateDto.getProductCategory()) ? matchCategory(productUpdateDto.getProductCategory()) : dbProduct.getProductCategory());
                dbProduct.setManufacturer(Objects.nonNull(productUpdateDto.getManufacturer()) ? matchManufacturer(productUpdateDto.getManufacturer()) : dbProduct.getManufacturer());
                dbProduct.setFeatured(Objects.nonNull(productUpdateDto.isFeatured()) ? productUpdateDto.isFeatured() : dbProduct.isFeatured());
                dbProduct.setFreeShipping(Objects.nonNull(productUpdateDto.isFreeShipping()) ? productUpdateDto.isFreeShipping() : dbProduct.isFreeShipping());
                dbProduct.setStoreLocation(Objects.nonNull(productUpdateDto.getStoreLocation()) ? productUpdateDto.getStoreLocation() : dbProduct.getStoreLocation());

                existingProducts.add(dbProduct);
            }else{
                nonExistentProducts.add(productUpdateDto);
            }
        }

        productRepository.saveAll(existingProducts);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("savedProducts", existingProducts);
        result.put("rejectedProducts", nonExistentProducts);

        return result;
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
