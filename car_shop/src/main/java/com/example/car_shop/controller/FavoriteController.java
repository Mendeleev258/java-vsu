package com.example.car_shop.controller;

import com.example.car_shop.request.FavoriteRequest;
import com.example.car_shop.response.FavoriteResponse;
import com.example.car_shop.entity.Car;
import com.example.car_shop.entity.UserFavoriteCar;
import com.example.car_shop.mapper.FavoriteMapper;
import com.example.car_shop.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final FavoriteMapper favoriteMapper;

    public FavoriteController(FavoriteService favoriteService, FavoriteMapper favoriteMapper) {
        this.favoriteService = favoriteService;
        this.favoriteMapper = favoriteMapper;
    }

    @PostMapping
    public FavoriteResponse addFavorite(@RequestBody FavoriteRequest request) {
        try {
            UserFavoriteCar favorite = favoriteService.addFavorite(
                    request.userId(),
                    request.carId()
            );
            return favoriteMapper.toResponse(favorite);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/{carId}")
    public void removeFavorite(@PathVariable UUID userId, @PathVariable UUID carId) {
        favoriteService.removeFavorite(userId, carId);
    }

    @GetMapping("/users/{userId}")
    public List<Car> getUserFavorites(@PathVariable UUID userId) {
        return favoriteService.getUserFavoriteCars(userId);
    }

    @GetMapping("/check/{userId}/{carId}")
    public boolean isFavorite(@PathVariable UUID userId, @PathVariable UUID carId) {
        return favoriteService.isFavorite(userId, carId);
    }

    @GetMapping("/count/user/{userId}")
    public int countUserFavorites(@PathVariable UUID userId) {
        return favoriteService.countFavoritesByUser(userId);
    }

    @GetMapping("/count/car/{carId}")
    public int countCarFavorites(@PathVariable UUID carId) {
        return favoriteService.countFavoritesByCar(carId);
    }

    @DeleteMapping("/users/{userId}")
    public void removeAllUserFavorites(@PathVariable UUID userId) {
        favoriteService.removeAllFavoritesByUser(userId);
    }

    @DeleteMapping("/cars/{carId}")
    public void removeAllCarFavorites(@PathVariable UUID carId) {
        favoriteService.removeAllFavoritesByCar(carId);
    }
}