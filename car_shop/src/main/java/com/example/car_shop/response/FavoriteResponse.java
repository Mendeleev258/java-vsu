package com.example.car_shop.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FavoriteResponse(
        UUID userId,
        UUID carId,
        OffsetDateTime addedAt
) {}