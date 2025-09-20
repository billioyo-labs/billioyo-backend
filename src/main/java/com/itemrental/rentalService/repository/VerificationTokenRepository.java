package com.itemrental.rentalService.repository;

import com.itemrental.rentalService.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {

}
