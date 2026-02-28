package com.venediktov.carservice.services;

import com.venediktov.carservice.model.User;
import com.venediktov.carservice.model.UserToken;
import com.venediktov.carservice.repositories.UserTokenRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserTokenRepository userTokenRepository;

    public AuthService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    @Cacheable(value = "usersByToken", key = "#token")
    public Optional<User> getUserByToken(String token) {
        return userTokenRepository.findByToken(token)
                .map(UserToken::getUser);
    }

    @Transactional
    @CacheEvict(value = "usersByToken", key = "#token")
    public void logout(String token) {
        userTokenRepository.deleteByToken(token);
    }
}
