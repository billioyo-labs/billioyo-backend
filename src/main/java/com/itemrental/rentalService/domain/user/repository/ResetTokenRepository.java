package com.itemrental.rentalService.domain.user.repository;

import com.itemrental.rentalService.domain.user.entity.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository  extends CrudRepository<ResetToken, String> {
}
