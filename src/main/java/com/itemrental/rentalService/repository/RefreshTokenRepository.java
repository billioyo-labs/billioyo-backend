package com.itemrental.rentalService.repository;

import com.itemrental.rentalService.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {
    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}