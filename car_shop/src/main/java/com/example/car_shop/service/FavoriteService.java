package com.example.car_shop.service;

import com.example.car_shop.entity.Car;
import com.example.car_shop.entity.User;
import com.example.car_shop.entity.UserFavoriteCar;
import com.example.car_shop.exception.NotFoundException;
import com.example.car_shop.exception.ValidationException;
import com.example.car_shop.repository.FavoriteRepository;
import com.example.car_shop.repository.UserRepository;
import com.example.car_shop.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           CarRepository carRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Transactional
    public UserFavoriteCar addFavorite(UUID userId, UUID carId) {
        // Проверяем существование пользователя и машины
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found with id: " + carId));

        // Проверяем, не добавлено ли уже в избранное
        if (favoriteRepository.existsByUserIdAndCarId(userId, carId)) {
            throw new ValidationException("Car already in favorites for this user");
        }

        UserFavoriteCar favorite = new UserFavoriteCar(userId, carId, OffsetDateTime.now());

        if (!favoriteRepository.create(favorite)) {
            throw new RuntimeException("Failed to add favorite");
        }

        return favorite;
    }

    @Transactional
    public void removeFavorite(UUID userId, UUID carId) {
        // Проверяем существование
        if (!favoriteRepository.existsByUserIdAndCarId(userId, carId)) {
            throw new NotFoundException("Favorite not found for user " + userId + " and car " + carId);
        }

        favoriteRepository.deleteByUserIdAndCarId(userId, carId);
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteCar> getUserFavorites(UUID userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Car> getUserFavoriteCars(UUID userId) {
        List<UUID> carIds = favoriteRepository.findByUserId(userId).stream()
                .map(UserFavoriteCar::getCarId)
                .toList();

        return carIds.stream()
                .map(carId -> carRepository.findById(carId)
                        .orElseThrow(() -> new NotFoundException("Car not found with id: " + carId)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(UUID userId, UUID carId) {
        return favoriteRepository.existsByUserIdAndCarId(userId, carId);
    }

    @Transactional(readOnly = true)
    public int countFavoritesByUser(UUID userId) {
        return favoriteRepository.countFavoritesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public int countFavoritesByCar(UUID carId) {
        return favoriteRepository.countFavoritesByCarId(carId);
    }

    @Transactional
    public void removeAllFavoritesByUser(UUID userId) {
        favoriteRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void removeAllFavoritesByCar(UUID carId) {
        favoriteRepository.deleteAllByCarId(carId);
    }
}