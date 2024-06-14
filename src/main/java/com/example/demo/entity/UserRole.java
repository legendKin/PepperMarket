package com.example.demo.entity;

public enum UserRole {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
