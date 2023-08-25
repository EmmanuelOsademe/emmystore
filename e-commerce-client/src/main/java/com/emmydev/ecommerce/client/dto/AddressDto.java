package com.emmydev.ecommerce.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Builder
@Data
public class AddressDto {

    private Integer zipCode = null;

    private String city = null;

    private String state = null;

    private  String country = null;

    public AddressDto getAddress(AddressDto addressDto){
        Integer zipCode = Objects.nonNull(addressDto.getZipCode()) ? addressDto.getZipCode() : this.zipCode;
        String city = Objects.nonNull(addressDto.getCity()) ? addressDto.getCity() : this.city;
        String state = Objects.nonNull(addressDto.getState()) ? addressDto.getState() : this.state;
        String country = Objects.nonNull(addressDto.getCountry()) ? addressDto.getCountry() : this.country;

        return AddressDto.builder()
                .zipCode(zipCode)
                .city(city)
                .state(state)
                .country(country)
                .build();
    }
}
