package com.ims.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN, STAFF;
    @JsonCreator
    public static Role fromString(String role) {
        if (role != null) {
            switch (role.toUpperCase()) {
                case "USER":
                    return STAFF;  // or ADMIN based on your logic
                case "STAFF":
                    return STAFF;
                case "ADMIN":
                    return ADMIN;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + role);
            }
        }
        throw new IllegalArgumentException("Role cannot be null");
    }
}
