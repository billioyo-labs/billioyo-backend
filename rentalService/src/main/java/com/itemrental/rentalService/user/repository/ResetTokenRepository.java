package com.itemrental.rentalService.user.repository;

import com.itemrental.rentalService.user.entity.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository  extends CrudRepository<ResetToken, String> {
}
