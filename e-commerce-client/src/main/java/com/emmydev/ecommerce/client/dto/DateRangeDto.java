package com.emmydev.ecommerce.client.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class DateRangeDto extends PageRequestDto {

    @NotBlank(message = "Please specify start date")
    private Date start;

    @NotBlank(message = "Please specify end date")
    private Date end;

    private Sort.Direction sort = Sort.Direction.DESC;

    @Override
    public Pageable getPageable(PageRequestDto pageRequestDto) {
        pageRequestDto.setSort(sort);
        return super.getPageable(pageRequestDto);
    }
}
