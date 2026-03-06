package com.example.demo.Reports;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_logs")
public class ReportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "DASHBOARD_SUMMARY_CSV"
    @Column(nullable = false)
    private String reportType;

    // Example: "dashboard-summary-12.csv"
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    public ReportLog() {}

    public ReportLog(String reportType, String fileName, LocalDateTime generatedAt) {
        this.reportType = reportType;
        this.fileName = fileName;
        this.generatedAt = generatedAt;
    }

    public Long getId() { return id; }
    public String getReportType() { return reportType; }
    public String getFileName() { return fileName; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}