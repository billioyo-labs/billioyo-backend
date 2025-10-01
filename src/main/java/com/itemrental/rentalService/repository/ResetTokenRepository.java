package com.itemrental.rentalService.repository;

import com.itemrental.rentalService.entity.ResetToken;
import org.springframework.data.repository.CrudRepository;

public interface ResetTokenRepository  extends CrudRepository<ResetToken, String> {
}
