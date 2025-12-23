package com.itemrental.rentalService.user.repository;

import com.itemrental.rentalService.user.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {

}
