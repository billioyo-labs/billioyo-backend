package com.itemrental.billioyo.domain.auth.repository;

import com.itemrental.billioyo.domain.auth.entity.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository extends CrudRepository<ResetToken, String> {
}
