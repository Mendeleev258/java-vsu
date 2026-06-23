package com.example.car_shop.repository;

import com.example.car_shop.entity.Car;
import com.example.car_shop.exception.NotFoundException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CarRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CarRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Car> findAll() {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars";
        return namedParameterJdbcTemplate.query(sql, CAR_ROW_MAPPER);
    }

    public Optional<Car> findById(UUID id) {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars WHERE id = :id";
        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), CAR_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    // пессимистик лок for update
    public Optional<Car> findByIdForUpdate(UUID id) {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars WHERE id = :id FOR UPDATE";
        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), CAR_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    // пессимистик лок - не ждать если заблокировано
    public Optional<Car> findByIdForUpdateNowait(UUID id) {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars WHERE id = :id FOR UPDATE NOWAIT";
        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), CAR_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    public Boolean create(Car car) {
        String sql = "INSERT INTO cars " +
                "(id, brand, model, year, price, horsepower, color, created_at, updated_at) " +
                "VALUES (:id, :brand, :model, :year, :price, :horsepower, :color, :createdAt, :updatedAt)";
        return namedParameterJdbcTemplate.update(sql, Map.of(
                "id", car.getId(),
                "brand", car.getBrand(),
                "model", car.getModel(),
                "year", car.getYear(),
                "price", car.getPrice(),
                "horsepower", car.getHorsepower(),
                "color", car.getColor(),
                "createdAt", car.getCreatedAt(),
                "updatedAt", car.getUpdatedAt()
        )) == 1;
    }

    public Boolean update(Car car) {
        String sql = "UPDATE cars " +
                "SET brand = :brand, " +
                "model = :model, " +
                "year = :year, " +
                "price = :price, " +
                "horsepower = :horsepower, " +
                "color = :color, " +
                "updated_at = :updatedAt " +
                "WHERE id = :id";
        int rows = namedParameterJdbcTemplate.update(sql, Map.of(
                "id", car.getId(),
                "brand", car.getBrand(),
                "model", car.getModel(),
                "year", car.getYear(),
                "price", car.getPrice(),
                "horsepower", car.getHorsepower(),
                "color", car.getColor(),
                "updatedAt", car.getUpdatedAt()
        ));
        return rows == 1;
    }

    public void delete(UUID id) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM user_favorite_cars WHERE car_id = :id",
                Map.of("id", id)
        );

        int rows = namedParameterJdbcTemplate.update(
                "DELETE FROM cars WHERE id = :id",
                Map.of("id", id)
        );

        if (rows == 0) {
            throw new NotFoundException("Car not found with id: " + id);
        }
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM cars WHERE id = :id";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("id", id),
                Integer.class
        );
        return count != null && count > 0;
    }

    public List<Car> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars WHERE price BETWEEN :minPrice AND :maxPrice";
        return namedParameterJdbcTemplate.query(
                sql,
                Map.of("minPrice", minPrice, "maxPrice", maxPrice),
                CAR_ROW_MAPPER
        );
    }

    public List<Car> findByYearBetween(Integer startYear, Integer endYear) {
        String sql = "SELECT id, brand, model, year, price, horsepower, color, created_at, updated_at " +
                "FROM cars WHERE year BETWEEN :startYear AND :endYear";
        return namedParameterJdbcTemplate.query(
                sql,
                Map.of("startYear", startYear, "endYear", endYear),
                CAR_ROW_MAPPER
        );
    }

    // проверяем есть ли активные блокировки на записи
    public boolean isLocked(UUID id) {
        String sql = "SELECT EXISTS (" +
                "SELECT 1 FROM pg_locks l " +
                "JOIN pg_class c ON l.relation = c.oid " +
                "WHERE c.relname = 'cars' " +
                "AND l.objid = :id::regclass " +
                "AND l.granted = true" +
                ")";
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("id", id),
                Boolean.class
        ));
    }

    private static final RowMapper<Car> CAR_ROW_MAPPER = (rs, rowNum) -> new Car(
            rs.getObject("id", UUID.class),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getBigDecimal("price"),
            rs.getInt("horsepower"),
            rs.getString("color"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );

    private static final RowMapper<Car> CAR_FULL_MAPPER = (rs, rowNum) -> new Car(
            rs.getObject("id", UUID.class),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getBigDecimal("price"),
            rs.getInt("horsepower"),
            rs.getString("color"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );
}