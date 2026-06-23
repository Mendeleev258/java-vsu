package com.example.car_shop.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserFavoriteCar {
    private UUID userId;
    private UUID carId;
    private OffsetDateTime addedAt;

    // Конструктор с обязательными полями
    public UserFavoriteCar(UUID userId, UUID carId) {
        this.userId = userId;
        this.carId = carId;
    }

    // Полный конструктор
    public UserFavoriteCar(UUID userId, UUID carId, OffsetDateTime addedAt) {
        this.userId = userId;
        this.carId = carId;
        this.addedAt = addedAt;
    }

    // Геттеры и сеттеры
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public OffsetDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(OffsetDateTime addedAt) {
        this.addedAt = addedAt;
    }
}