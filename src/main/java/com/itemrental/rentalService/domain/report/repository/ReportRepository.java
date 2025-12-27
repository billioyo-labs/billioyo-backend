package com.itemrental.rentalService.domain.report.repository;

import com.itemrental.rentalService.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
