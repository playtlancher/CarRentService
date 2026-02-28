package com.venediktov.carservice.dto;

import com.venediktov.carservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String role;

    public static UserDto from(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
}
