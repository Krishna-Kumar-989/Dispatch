package com.clark.roper.Dispatch.repository;

import com.clark.roper.Dispatch.entity.Report;
import com.clark.roper.Dispatch.enums.ReportStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  Page<Report> findByStatus(ReportStatusEnum status, Pageable pageable);

  Page<Report> findAll(Pageable pageable);
}
