package com.example.car_shop.repository;

import com.example.car_shop.entity.Role;
import com.example.car_shop.entity.User;
import com.example.car_shop.exception.NotFoundException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<User> findAll() {
        String sql = "SELECT id, username, email, role, created_at, updated_at, version " +
                "FROM users";
        return namedParameterJdbcTemplate.query(sql, USER_ROW_MAPPER);
    }

    public Optional<User> findById(UUID id) {
        String sql = "SELECT id, username, email, password_hash, role, created_at, updated_at, version " +
                "FROM users WHERE id = :id";
        return namedParameterJdbcTemplate.query(sql, Map.of("id", id), USER_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, email, password_hash, role, created_at, updated_at, version " +
                "FROM users WHERE username = :username";
        return namedParameterJdbcTemplate.query(sql, Map.of("username", username), USER_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, username, email, password_hash, role, created_at, updated_at, version " +
                "FROM users WHERE email = :email";
        return namedParameterJdbcTemplate.query(sql, Map.of("email", email), USER_FULL_MAPPER)
                .stream()
                .findFirst();
    }

    public Boolean create(User user) {
        String sql = "INSERT INTO users " +
                "(id, username, email, password_hash, role, created_at, updated_at, version) " +
                "VALUES (:id, :username, :email, :passwordHash, :role, :createdAt, :updatedAt, :version)";
        return namedParameterJdbcTemplate.update(sql, Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "passwordHash", user.getPasswordHash(),
                "role", user.getRole().name(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt(),
                "version", user.getVersion() != null ? user.getVersion() : 0L
        )) == 1;
    }

    // проверяем версию для оптимистичной блокировки
    public Boolean update(User user) {
        String sql = "UPDATE users " +
                "SET username = :username, " +
                "email = :email, " +
                "password_hash = :passwordHash, " +
                "role = :role, " +
                "updated_at = :updatedAt, " +
                "version = :newVersion " +
                "WHERE id = :id AND version = :oldVersion";

        int rows = namedParameterJdbcTemplate.update(sql, Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "passwordHash", user.getPasswordHash(),
                "role", user.getRole().name(),
                "updatedAt", user.getUpdatedAt(),
                "newVersion", user.getVersion() + 1,
                "oldVersion", user.getVersion()
        ));
        return rows == 1;
    }

    public void delete(UUID id) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM user_favorite_cars WHERE user_id = :id",
                Map.of("id", id)
        );

        int rows = namedParameterJdbcTemplate.update(
                "DELETE FROM users WHERE id = :id",
                Map.of("id", id)
        );

        if (rows == 0) {
            throw new NotFoundException("User not found with id: " + id);
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = :username";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("username", username),
                Integer.class
        );
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = :email";
        Integer count = namedParameterJdbcTemplate.queryForObject(
                sql,
                Map.of("email", email),
                Integer.class
        );
        return count != null && count > 0;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
        rs.getObject("id", UUID.class),
        rs.getString("username"),
        rs.getString("email"),
        null,
        Role.valueOf(rs.getString("role")),
        rs.getObject("created_at", OffsetDateTime.class),
        rs.getObject("updated_at", OffsetDateTime.class),
        rs.getLong("version")
    );

    private static final RowMapper<User> USER_FULL_MAPPER = (rs, rowNum) -> new User(
            rs.getObject("id", UUID.class),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            Role.valueOf(rs.getString("role")),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class),
            rs.getLong("version")
    );
}