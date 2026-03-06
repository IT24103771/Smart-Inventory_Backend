package com.example.demo.Reports;

import com.example.demo.Dashboard.DashboardService;
import com.example.demo.Dashboard.DashboardSummaryResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class ReportService {

    private final DashboardService dashboardService;
    private final ReportLogRepository reportLogRepository;

    public ReportService(DashboardService dashboardService,
                         ReportLogRepository reportLogRepository) {
        this.dashboardService = dashboardService;
        this.reportLogRepository = reportLogRepository;
    }

    public GeneratedReport generateDashboardSummaryCsv() {

        // 1) get dashboard data
        DashboardSummaryResponse s = dashboardService.getSummary();

        // 2) build CSV
        String csv = ""
                + "Dashboard Report,Summary\n"
                + "Generated At," + LocalDateTime.now() + "\n"
                + "\n"
                + "Metric,Value\n"
                + "Total Products," + s.getTotalProducts() + "\n"
                + "Total Batches," + s.getTotalBatches() + "\n"
                + "Total Stock Qty," + s.getTotalStockQty() + "\n"
                + "Low Stock Batches," + s.getLowStockBatches() + "\n"
                + "Expiring Soon Batches (7 days)," + s.getExpiringSoonBatches() + "\n"
                + "Expired Batches," + s.getExpiredBatches() + "\n"
                + "Sales Today Qty," + s.getSalesTodayQty() + "\n"
                + "Active Discounts," + s.getActiveDiscounts() + "\n";

        byte[] fileBytes = csv.getBytes(StandardCharsets.UTF_8);

        // 3) save report log in DB (reportId is the DB id)
        LocalDateTime now = LocalDateTime.now();
        String fileName = "dashboard-summary-" + now.toLocalDate() + ".csv";

        ReportLog saved = reportLogRepository.save(
                new ReportLog("DASHBOARD_SUMMARY_CSV", fileName, now)
        );

        // 4) return everything (bytes + id + metadata)
        return new GeneratedReport(saved.getId(), saved.getReportType(), saved.getFileName(), saved.getGeneratedAt(), fileBytes);
    }
}