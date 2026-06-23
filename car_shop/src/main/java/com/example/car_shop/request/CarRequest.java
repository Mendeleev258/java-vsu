package com.example.car_shop.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CarRequest(
        UUID id,
        String brand,
        String model,
        Integer year,
        BigDecimal price,
        Integer horsepower,
        String color
) {}