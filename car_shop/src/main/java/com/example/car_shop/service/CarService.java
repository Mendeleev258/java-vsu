package com.example.car_shop.service;

import com.example.car_shop.entity.Car;
import com.example.car_shop.exception.NotFoundException;
import com.example.car_shop.exception.ValidationException;
import com.example.car_shop.repository.CarRepository;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Transactional
    public Car createCar(Car car) {
        // Валидация
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new ValidationException("Brand is required");
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new ValidationException("Model is required");
        }
        if (car.getPrice() != null && car.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Price cannot be negative");
        }
        if (car.getHorsepower() != null && car.getHorsepower() <= 0) {
            throw new ValidationException("Horsepower must be positive");
        }

        car.setCreatedAt(OffsetDateTime.now());
        car.setUpdatedAt(OffsetDateTime.now());

        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }

        if (!carRepository.create(car)) {
            throw new RuntimeException("Failed to create car");
        }

        return carRepository.findById(car.getId())
                .orElseThrow(() -> new RuntimeException("Car not found after creation"));
    }

    @Transactional(readOnly = true)
    public Car getCarById(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Transactional
    public Car updateCar(UUID id, Car updatedCar) {
        try {
            // блокируем запись
            Car existing = carRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));

            // Валидация
            if (updatedCar.getBrand() != null && updatedCar.getBrand().trim().isEmpty()) {
                throw new ValidationException("Brand cannot be empty");
            }
            if (updatedCar.getModel() != null && updatedCar.getModel().trim().isEmpty()) {
                throw new ValidationException("Model cannot be empty");
            }
            if (updatedCar.getPrice() != null && updatedCar.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Price cannot be negative");
            }
            if (updatedCar.getHorsepower() != null && updatedCar.getHorsepower() <= 0) {
                throw new ValidationException("Horsepower must be positive");
            }

            // Обновляем поля
            if (updatedCar.getBrand() != null) {
                existing.setBrand(updatedCar.getBrand());
            }
            if (updatedCar.getModel() != null) {
                existing.setModel(updatedCar.getModel());
            }
            if (updatedCar.getYear() != null) {
                existing.setYear(updatedCar.getYear());
            }
            if (updatedCar.getPrice() != null) {
                existing.setPrice(updatedCar.getPrice());
            }
            if (updatedCar.getHorsepower() != null) {
                existing.setHorsepower(updatedCar.getHorsepower());
            }
            if (updatedCar.getColor() != null) {
                existing.setColor(updatedCar.getColor());
            }
            existing.setUpdatedAt(OffsetDateTime.now());

            if (!carRepository.update(existing)) {
                throw new RuntimeException("Failed to update car");
            }

            return carRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Car not found after update"));

        } catch (PessimisticLockingFailureException e) {
            throw new ValidationException("Car is currently locked by another transaction. Please try again later.");
        }
    }

    @Transactional
    public void deleteCar(UUID id) {
        try {
            // блокируем запись перед удалением
            Car existing = carRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));

            carRepository.delete(id);

        } catch (PessimisticLockingFailureException e) {
            throw new ValidationException("Car is currently locked by another transaction. Please try again later.");
        }
    }

    @Transactional
    public Car updateCarPrice(UUID id, BigDecimal newPrice) {
        try {
            // пессимистик лок
            Car existing = carRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));

            if (newPrice == null) {
                throw new ValidationException("Price cannot be null");
            }
            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Price cannot be negative");
            }

            existing.setPrice(newPrice);
            existing.setUpdatedAt(OffsetDateTime.now());

            if (!carRepository.update(existing)) {
                throw new RuntimeException("Failed to update car price");
            }

            return carRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Car not found after update"));

        } catch (PessimisticLockingFailureException e) {
            throw new ValidationException("Car is currently locked by another transaction. Please try again later.");
        }
    }

    @Transactional
    public Car updateCarHorsepower(UUID id, Integer newHorsepower) {
        try {
            // пессимистик лок
            Car existing = carRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));

            if (newHorsepower == null || newHorsepower <= 0) {
                throw new ValidationException("Horsepower must be positive");
            }

            existing.setHorsepower(newHorsepower);
            existing.setUpdatedAt(OffsetDateTime.now());

            if (!carRepository.update(existing)) {
                throw new RuntimeException("Failed to update car horsepower");
            }

            return carRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Car not found after update"));

        } catch (PessimisticLockingFailureException e) {
            throw new ValidationException("Car is currently locked by another transaction. Please try again later.");
        }
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new ValidationException("Min price cannot be greater than max price");
        }
        return carRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByYearRange(Integer startYear, Integer endYear) {
        if (startYear == null || endYear == null) {
            throw new ValidationException("Both start and end year are required");
        }
        if (startYear > endYear) {
            throw new ValidationException("Start year cannot be greater than end year");
        }
        return carRepository.findByYearBetween(startYear, endYear);
    }

    @Transactional(readOnly = true)
    public boolean isCarLocked(UUID id) {
        return carRepository.isLocked(id);
    }
}