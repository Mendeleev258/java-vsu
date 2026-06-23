package com.example.car_shop.request;

import java.util.UUID;

public record FavoriteRequest(
        UUID userId,
        UUID carId
) {}