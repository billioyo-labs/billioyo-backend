package com.itemrental.rentalService.domain.user.repository;

import com.itemrental.rentalService.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {
    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}