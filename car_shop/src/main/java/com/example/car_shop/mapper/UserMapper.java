package com.example.car_shop.mapper;

import com.example.car_shop.request.UserRequest;
import com.example.car_shop.response.UserResponse;
import com.example.car_shop.entity.Role;
import com.example.car_shop.entity.User;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        return new User(
                UUID.randomUUID(),
                request.username(),
                request.email(),
                request.password(), // будет захеширован в сервисе
                request.role() != null ? request.role() : Role.USER,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                0L
        );
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getVersion()
        );
    }

    public User updateEntity(User existing, UserRequest request) {
        if (request.username() != null) {
            existing.setUsername(request.username());
        }
        if (request.email() != null) {
            existing.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isEmpty()) {
            existing.setPasswordHash(request.password());
        }
        if (request.role() != null) {
            existing.setRole(request.role());
        }
        existing.setUpdatedAt(OffsetDateTime.now());
        existing.setVersion(existing.getVersion() + 1);
        return existing;
    }
}