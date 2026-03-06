package com.example.demo.Reports;

import java.time.LocalDateTime;

public class GeneratedReport {
    private Long reportId;
    private String reportType;
    private String fileName;
    private LocalDateTime generatedAt;
    private byte[] fileBytes;

    public GeneratedReport(Long reportId, String reportType, String fileName, LocalDateTime generatedAt, byte[] fileBytes) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.fileName = fileName;
        this.generatedAt = generatedAt;
        this.fileBytes = fileBytes;
    }

    public Long getReportId() { return reportId; }
    public String getReportType() { return reportType; }
    public String getFileName() { return fileName; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public byte[] getFileBytes() { return fileBytes; }
}