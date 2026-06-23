package com.example.car_shop.mapper;

import com.example.car_shop.request.CarRequest;
import com.example.car_shop.response.CarResponse;
import com.example.car_shop.entity.Car;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class CarMapper {

    public Car toEntity(CarRequest request) {
        if (request == null) {
            return null;
        }

        return new Car(
                request.id() != null ? request.id() : UUID.randomUUID(),
                request.brand(),
                request.model(),
                request.year(),
                request.price(),
                request.horsepower(),
                request.color(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    public CarResponse toResponse(Car car) {
        if (car == null) {
            return null;
        }

        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getPrice(),
                car.getHorsepower(),
                car.getColor(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }

    public Car updateEntity(Car existing, CarRequest request) {
        if (request.brand() != null) {
            existing.setBrand(request.brand());
        }
        if (request.model() != null) {
            existing.setModel(request.model());
        }
        if (request.year() != null) {
            existing.setYear(request.year());
        }
        if (request.price() != null) {
            existing.setPrice(request.price());
        }
        if (request.horsepower() != null) {
            existing.setHorsepower(request.horsepower());
        }
        if (request.color() != null) {
            existing.setColor(request.color());
        }
        existing.setUpdatedAt(OffsetDateTime.now());
        return existing;
    }
}