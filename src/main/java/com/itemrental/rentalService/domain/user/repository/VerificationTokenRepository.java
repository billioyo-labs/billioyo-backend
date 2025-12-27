package com.itemrental.rentalService.domain.user.repository;

import com.itemrental.rentalService.domain.user.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {

}
