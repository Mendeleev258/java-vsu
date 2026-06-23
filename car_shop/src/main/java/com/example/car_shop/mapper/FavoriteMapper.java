package com.example.car_shop.mapper;

import com.example.car_shop.request.FavoriteRequest;
import com.example.car_shop.response.FavoriteResponse;
import com.example.car_shop.entity.UserFavoriteCar;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class FavoriteMapper {

    public UserFavoriteCar toEntity(FavoriteRequest request) {
        if (request == null) {
            return null;
        }

        return new UserFavoriteCar(
                request.userId(),
                request.carId(),
                OffsetDateTime.now()
        );
    }

    public UserFavoriteCar toEntity(FavoriteRequest request, OffsetDateTime addedAt) {
        if (request == null) {
            return null;
        }

        return new UserFavoriteCar(
                request.userId(),
                request.carId(),
                addedAt != null ? addedAt : OffsetDateTime.now()
        );
    }

    public FavoriteResponse toResponse(UserFavoriteCar favorite) {
        if (favorite == null) {
            return null;
        }

        return new FavoriteResponse(
                favorite.getUserId(),
                favorite.getCarId(),
                favorite.getAddedAt()
        );
    }

    public FavoriteRequest toRequest(java.util.UUID userId, java.util.UUID carId) {
        if (userId == null || carId == null) {
            return null;
        }

        return new FavoriteRequest(userId, carId);
    }

    public UserFavoriteCar updateEntity(UserFavoriteCar existing, FavoriteRequest request) {
        if (existing == null || request == null) {
            return existing;
        }

        // Обновляем только userId и carId (addedAt не меняется)
        existing.setUserId(request.userId());
        existing.setCarId(request.carId());

        return existing;
    }
}