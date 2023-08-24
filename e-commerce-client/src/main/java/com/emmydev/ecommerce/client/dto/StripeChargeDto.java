package com.emmydev.ecommerce.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeChargeDto {

    public enum Currency {
        EUR, USD;
    }

    private String description;
    private Long amount;

    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}
