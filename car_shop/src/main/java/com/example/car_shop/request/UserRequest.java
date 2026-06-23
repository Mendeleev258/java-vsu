package com.example.car_shop.request;

import com.example.car_shop.entity.Role;

public record UserRequest(
        String username,
        String email,
        String password,
        Role role
) {}