package com.itemrental.rentalService.user.repository;

import com.itemrental.rentalService.user.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {
    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}