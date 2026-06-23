package com.example.car_shop.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CarResponse(
        UUID id,
        String brand,
        String model,
        Integer year,
        BigDecimal price,
        Integer horsepower,
        String color,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}