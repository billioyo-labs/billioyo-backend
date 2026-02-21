package com.itemrental.billioyo.domain.auth.repository;

import com.itemrental.billioyo.domain.auth.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {

}
