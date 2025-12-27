package com.itemrental.rentalService.domain.user.repository;

import com.itemrental.rentalService.domain.user.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
