package com.example.car_shop.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Car {
    private UUID id;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal price;
    private Integer horsepower;
    private String color;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Конструктор с обязательными полями
    public Car(UUID id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    // Полный конструктор
    public Car(UUID id, String brand, String model, Integer year, BigDecimal price,
               Integer horsepower, String color, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.horsepower = horsepower;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Вспомогательные методы
    public String getFullName() {
        return brand + " " + model;
    }

    public boolean isNew() {
        return year != null && year >= 2020;
    }

    public boolean isAffordable() {
        return price != null && price.compareTo(BigDecimal.valueOf(30000)) <= 0;
    }
}