package com.example.car_shop.controller;

import com.example.car_shop.request.CarRequest;
import com.example.car_shop.response.CarResponse;
import com.example.car_shop.entity.Car;
import com.example.car_shop.exception.ValidationException;
import com.example.car_shop.mapper.CarMapper;
import com.example.car_shop.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;
    private final CarMapper carMapper;

    public CarController(CarService carService, CarMapper carMapper) {
        this.carService = carService;
        this.carMapper = carMapper;
    }

    @GetMapping
    public List<CarResponse> findAll() {
        return carService.getAllCars().stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CarResponse findById(@PathVariable UUID id) {
        Car car = carService.getCarById(id);
        return carMapper.toResponse(car);
    }

    @GetMapping("/search")
    public List<CarResponse> search(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear) {

        List<Car> cars;
        if (minPrice != null || maxPrice != null) {
            cars = carService.getCarsByPriceRange(minPrice, maxPrice);
        } else if (startYear != null && endYear != null) {
            cars = carService.getCarsByYearRange(startYear, endYear);
        } else {
            cars = carService.getAllCars();
        }

        return cars.stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @PostMapping
    public CarResponse createCar(@RequestBody CarRequest request) {
        Car car = carMapper.toEntity(request);
        Car created = carService.createCar(car);
        return carMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public CarResponse updateCar(@PathVariable UUID id, @RequestBody CarRequest request) {
        try {
            Car car = carMapper.toEntity(request);
            Car updated = carService.updateCar(id, car);
            return carMapper.toResponse(updated);
        } catch (ValidationException e) {
            // типа умный 409 еррор код кину
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable UUID id) {
        carService.deleteCar(id);
    }

    @PatchMapping("/{id}/price")
    public CarResponse updatePrice(@PathVariable UUID id, @RequestParam BigDecimal price) {
        try {
            Car updated = carService.updateCarPrice(id, price);
            return carMapper.toResponse(updated);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PatchMapping("/{id}/horsepower")
    public CarResponse updateHorsepower(@PathVariable UUID id, @RequestParam Integer horsepower) {
        try {
            Car updated = carService.updateCarHorsepower(id, horsepower);
            return carMapper.toResponse(updated);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}/locked")
    public boolean isLocked(@PathVariable UUID id) {
        return carService.isCarLocked(id);
    }
}