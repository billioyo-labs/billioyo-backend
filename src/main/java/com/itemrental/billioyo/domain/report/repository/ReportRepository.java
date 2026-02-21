package com.itemrental.billioyo.domain.report.repository;

import com.itemrental.billioyo.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
