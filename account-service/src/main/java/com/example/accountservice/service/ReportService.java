package com.example.accountservice.service;

import com.example.accountservice.dto.response.ReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportResponseDTO> getReport(LocalDate startDate, LocalDate endDate, Long customerId);
}
