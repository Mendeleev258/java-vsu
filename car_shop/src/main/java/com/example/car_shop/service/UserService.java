package com.example.car_shop.service;

import com.example.car_shop.entity.Role;
import com.example.car_shop.entity.User;
import com.example.car_shop.exception.NotFoundException;
import com.example.car_shop.exception.OptimisticLockException;
import com.example.car_shop.exception.ValidationException;
import com.example.car_shop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ValidationException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email already exists: " + user.getEmail());
        }

        // Хешируем пароль
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        // Устанавливаем роль по умолчанию
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // Устанавливаем временные метки и версию
        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setVersion(0L);

        if (!userRepository.create(user)) {
            throw new RuntimeException("Failed to create user");
        }

        return userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found after creation"));
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(UUID id, User updatedUser) {
        // оптимистик лок - читаем с версией
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // Сохраняем текущую версию для проверки
        Long currentVersion = existing.getVersion();

        // Проверка уникальности username
        if (!existing.getUsername().equals(updatedUser.getUsername()) &&
                userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new ValidationException("Username already exists: " + updatedUser.getUsername());
        }

        // Проверка уникальности email
        if (!existing.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new ValidationException("Email already exists: " + updatedUser.getEmail());
        }

        // Обновляем поля
        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setUpdatedAt(OffsetDateTime.now());

        // Обновляем пароль, если он передан
        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }

        existing.setVersion(currentVersion + 1);

        // Выполняем обновление с проверкой версии
        boolean updated = userRepository.update(existing);
        if (!updated) {
            throw new OptimisticLockException(
                    "User was modified by another transaction. " +
                            "Current version: " + currentVersion +
                            ". Please refresh and retry."
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found after update"));
    }

    @Transactional
    public void deleteUser(UUID id) {
        // Проверяем существование
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User promoteToAdmin(UUID id) {
        // оптимистик лок
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        Long currentVersion = existing.getVersion();

        existing.setRole(Role.ADMIN);
        existing.setUpdatedAt(OffsetDateTime.now());
        existing.setVersion(currentVersion + 1);

        boolean updated = userRepository.update(existing);
        if (!updated) {
            throw new OptimisticLockException(
                    "User was modified by another transaction. Please retry."
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found after update"));
    }

    @Transactional
    public User demoteToUser(UUID id) {
        // оптимистик лок
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        Long currentVersion = existing.getVersion();

        existing.setRole(Role.USER);
        existing.setUpdatedAt(OffsetDateTime.now());
        existing.setVersion(currentVersion + 1);

        boolean updated = userRepository.update(existing);
        if (!updated) {
            throw new OptimisticLockException(
                    "User was modified by another transaction. Please retry."
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found after update"));
    }

    @Transactional
    public User updatePassword(UUID id, String newPassword) {
        // 🔒 ОПТИМИСТИЧЕСКАЯ БЛОКИРОВКА
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }

        Long currentVersion = existing.getVersion();

        existing.setPasswordHash(passwordEncoder.encode(newPassword));
        existing.setUpdatedAt(OffsetDateTime.now());
        existing.setVersion(currentVersion + 1);

        boolean updated = userRepository.update(existing);
        if (!updated) {
            throw new OptimisticLockException(
                    "User was modified by another transaction. Please retry."
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found after update"));
    }
}