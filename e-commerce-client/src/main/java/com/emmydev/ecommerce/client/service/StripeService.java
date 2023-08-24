package com.emmydev.ecommerce.client.service;

import com.emmydev.ecommerce.client.dto.StripeChargeDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = secretKey;
    }

    public Charge charge(StripeChargeDto chargeDto) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeDto.getAmount());
        chargeParams.put("currency", chargeDto.getCurrency());
        chargeParams.put("description", chargeDto.getDescription());
        chargeParams.put("source", chargeDto.getStripeToken());

        return Charge.create(chargeParams);
    }
}
