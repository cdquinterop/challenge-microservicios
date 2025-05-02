package com.example.accountservice.controller;

import com.example.accountservice.dto.response.ReportResponseDTO;
import com.example.accountservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Generate transaction report by customer and date range")
    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> getReport(
            @Parameter(description = "Start date in format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date in format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Customer ID")
            @RequestParam Long customerId
    ) {
        List<ReportResponseDTO> reports = reportService.getReport(startDate, endDate, customerId);
        return ResponseEntity.ok(reports);
    }
}
