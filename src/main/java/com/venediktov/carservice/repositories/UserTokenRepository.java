package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.UserToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);
    void deleteByToken(String token);
}
