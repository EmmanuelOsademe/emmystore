package com.emmydev.ecommerce.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "default")
@ConfigurationPropertiesScan
public class DefaultProperties {

    private int productsPerPage;

    public int getProductsPerPage() {
        return productsPerPage;
    }

    public void setProductsPerPage(int productsPerPage) {
        this.productsPerPage = productsPerPage;
    }
}
