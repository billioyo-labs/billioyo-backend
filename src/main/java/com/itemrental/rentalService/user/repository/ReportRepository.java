package com.itemrental.rentalService.user.repository;

import com.itemrental.rentalService.user.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
