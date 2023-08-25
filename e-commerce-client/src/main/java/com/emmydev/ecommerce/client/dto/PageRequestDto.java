package com.emmydev.ecommerce.client.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Data
public class PageRequestDto {

    @Value("${PAGE_NUMBER}")
    private int pageNumber;

    @Value("${PAGE_SIZE}")
    private int pageSize;
    private Sort.Direction sort = Sort.Direction.ASC;

    private String sortBy = "id";

    public Pageable getPageable(PageRequestDto pageRequestDto){
        int page = Objects.nonNull(pageRequestDto.getPageNumber()) ? pageRequestDto.getPageNumber() : this.pageNumber;
        int size = Objects.nonNull(pageRequestDto.getPageSize()) ? pageRequestDto.getPageSize() : this.pageSize;
        Sort.Direction sort = Objects.nonNull(pageRequestDto.getSort()) ? pageRequestDto.getSort() : this.sort;
        String sortBy = Objects.nonNull(pageRequestDto.getSortBy()) ? pageRequestDto.getSortBy() : this.sortBy;

        //PageRequest request = PageRequest.of(page, size);
        PageRequest request = PageRequest.of(page, size, sort, sortBy);
        return request;
    }

}
