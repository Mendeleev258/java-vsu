package com.example.car_shop.repository;

import com.example.car_shop.entity.UserFavoriteCar;
import com.example.car_shop.exception.ValidationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class FavoriteRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FavoriteRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<UserFavoriteCar> findByUserId(UUID userId) {
        String sql = "SELECT user_id, car_id, added_at FROM user_favorite_cars WHERE user_id = :userId";
        return namedParameterJdbcTemplate.query(sql, Map.of("userId", userId), FAVORITE_ROW_MAPPER);
    }

    public List<UserFavoriteCar> findByCarId(UUID carId) {
        String sql = "SELECT user_id, car_id, added_at FROM user_favorite_cars WHERE car_id = :carId";
        return namedParameterJdbcTemplate.query(sql, Map.of("carId", carId), FAVORITE_ROW_MAPPER);
    }

    public UserFavoriteCar findByUserIdAndCarId(UUID userId, UUID carId) {
        String sql = "SELECT user_id, car_id, added_at FROM user_favorite_cars " +
                "WHERE user_id = :userId AND car_id = :carId";
        List<UserFavoriteCar> favorites = namedParameterJdbcTemplate.query(
                sql,
                Map.of("userId", userId, "carId", carId),
                FAVORITE_ROW_MAPPER
        );
        if (favorites.size() > 1) {
            throw new ValidationException("Duplicate favorite entry found");
        }
        return favorites.stream().findFirst().orElse(null);
    }

    public boolean existsByUserIdAndCarId(UUID userId, UUID carId) {
        String sql = "SELECT COUNT(*) FROM user_favorite_cars " +
                "WHERE user_id = :userId AND car_id = :carId";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("userId", userId, "carId", carId),
                Integer.class
        );
        return count != null && count > 0;
    }

    public Boolean create(UserFavoriteCar favorite) {
        String sql = "INSERT INTO user_favorite_cars (user_id, car_id, added_at) " +
                "VALUES (:userId, :carId, :addedAt)";
        return namedParameterJdbcTemplate.update(sql, Map.of(
                "userId", favorite.getUserId(),
                "carId", favorite.getCarId(),
                "addedAt", favorite.getAddedAt()
        )) == 1;
    }

    public void deleteByUserIdAndCarId(UUID userId, UUID carId) {
        int rows = namedParameterJdbcTemplate.update(
                "DELETE FROM user_favorite_cars WHERE user_id = :userId AND car_id = :carId",
                Map.of("userId", userId, "carId", carId)
        );

        if (rows == 0) {
            throw new ValidationException("Favorite entry not found");
        }
    }

    public void deleteAllByUserId(UUID userId) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM user_favorite_cars WHERE user_id = :userId",
                Map.of("userId", userId)
        );
    }

    public void deleteAllByCarId(UUID carId) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM user_favorite_cars WHERE car_id = :carId",
                Map.of("carId", carId)
        );
    }

    public int countFavoritesByCarId(UUID carId) {
        String sql = "SELECT COUNT(*) FROM user_favorite_cars WHERE car_id = :carId";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("carId", carId),
                Integer.class
        );
        return count != null ? count : 0;
    }

    public int countFavoritesByUserId(UUID userId) {
        String sql = "SELECT COUNT(*) FROM user_favorite_cars WHERE user_id = :userId";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("userId", userId),
                Integer.class
        );
        return count != null ? count : 0;
    }

    private static final RowMapper<UserFavoriteCar> FAVORITE_ROW_MAPPER = (rs, rowNum) -> new UserFavoriteCar(
            rs.getObject("user_id", UUID.class),
            rs.getObject("car_id", UUID.class),
            rs.getObject("added_at", OffsetDateTime.class)
    );
}