package com.itemrental.billioyo.domain.auth.repository;

import com.itemrental.billioyo.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}