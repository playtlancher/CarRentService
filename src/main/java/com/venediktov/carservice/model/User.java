package com.venediktov.carservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @javax.persistence.OneToMany(mappedBy = "user", cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<UserToken> tokens = new java.util.HashSet<>();

    public enum UserRole {
        ADMIN,
        USER
    }
}
