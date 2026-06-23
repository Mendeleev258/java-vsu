package com.example.car_shop.controller;

import com.example.car_shop.request.UserRequest;
import com.example.car_shop.response.UserResponse;
import com.example.car_shop.entity.User;
import com.example.car_shop.exception.OptimisticLockException;
import com.example.car_shop.mapper.UserMapper;
import com.example.car_shop.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.getAllUsers().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return userMapper.toResponse(user);
    }

    @GetMapping("/search")
    public UserResponse findByUsername(@RequestParam String username) {
        User user = userService.getUserByUsername(username);
        return userMapper.toResponse(user);
    }

    @PermitAll
    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        User created = userService.createUser(user);
        return userMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id, @RequestBody UserRequest request) {
        try {
            User user = userMapper.toEntity(request);
            User updated = userService.updateUser(id, user);
            return userMapper.toResponse(updated);
        } catch (OptimisticLockException e) {
            // типа умный 409 еррор код кину
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/role")
    public UserResponse changeRole(@PathVariable UUID id, @RequestParam String role) {
        try {
            User updated;
            if ("ADMIN".equalsIgnoreCase(role)) {
                updated = userService.promoteToAdmin(id);
            } else {
                updated = userService.demoteToUser(id);
            }
            return userMapper.toResponse(updated);
        } catch (OptimisticLockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PatchMapping("/{id}/password")
    public UserResponse changePassword(@PathVariable UUID id, @RequestParam String newPassword) {
        try {
            User updated = userService.updatePassword(id, newPassword);
            return userMapper.toResponse(updated);
        } catch (OptimisticLockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}