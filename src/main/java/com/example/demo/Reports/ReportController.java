package com.example.demo.Reports;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportLogRepository reportLogRepository;

    public ReportController(ReportService reportService,
                            ReportLogRepository reportLogRepository) {
        this.reportService = reportService;
        this.reportLogRepository = reportLogRepository;
    }

    // PDF DOWNLOAD + SAVE REPORT LOG
    @GetMapping(value = "/dashboard-summary.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadDashboardSummaryPdf() {

        GeneratedReport report = reportService.generateDashboardSummaryPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\"")
                .header("X-Report-Id", String.valueOf(report.getReportId()))
                .contentType(MediaType.APPLICATION_PDF)
                .body(report.getFileBytes());
    }

    // OPTIONAL: keep CSV too
    @GetMapping(value = "/dashboard-summary.csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadDashboardSummaryCsv() {

        GeneratedReport report = reportService.generateDashboardSummaryCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\"")
                .header("X-Report-Id", String.valueOf(report.getReportId()))
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(report.getFileBytes());
    }

    // OPTIONAL: list report history
    @GetMapping("/history")
    public ResponseEntity<?> history() {
        return ResponseEntity.ok(reportLogRepository.findAll());
    }
}