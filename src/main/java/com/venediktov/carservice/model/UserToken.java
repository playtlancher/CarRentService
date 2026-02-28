package com.venediktov.carservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "user_tokens")
public class UserToken extends BaseEntity {

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
