package com.example.car_shop.response;

import com.example.car_shop.entity.Role;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Long version
) {}