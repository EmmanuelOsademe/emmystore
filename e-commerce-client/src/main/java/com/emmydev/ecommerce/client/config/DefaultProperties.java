package com.emmydev.ecommerce.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "default")
@Configuration
@Getter
@Setter
public class DefaultProperties {
    private int pageNumber;
    private int productsSize;
}
