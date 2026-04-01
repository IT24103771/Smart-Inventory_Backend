package com.example.demo.Reports;

import com.example.demo.Dashboard.DashboardService;
import com.example.demo.Dashboard.DashboardSummaryResponse;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
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

    public GeneratedReport generateDashboardSummaryPdf() {

        // 1) get dashboard data
        DashboardSummaryResponse s = dashboardService.getSummary();

        // 2) build PDF in memory
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        LocalDateTime now = LocalDateTime.now();

        document.add(new Paragraph("Dashboard Summary Report"));
        document.add(new Paragraph("Generated At: " + now));
        document.add(new Paragraph(" "));

        Table table = new Table(2);

        table.addCell("Metric");
        table.addCell("Value");

        table.addCell("Total Products");
        table.addCell(String.valueOf(s.getTotalProducts()));

        table.addCell("Total Batches");
        table.addCell(String.valueOf(s.getTotalBatches()));

        table.addCell("Total Stock Qty");
        table.addCell(String.valueOf(s.getTotalStockQty()));

        table.addCell("Low Stock Batches");
        table.addCell(String.valueOf(s.getLowStockBatches()));

        table.addCell("Expiring Soon Batches (7 days)");
        table.addCell(String.valueOf(s.getExpiringSoonBatches()));

        table.addCell("Expired Batches");
        table.addCell(String.valueOf(s.getExpiredBatches()));

        table.addCell("Sales Today Qty");
        table.addCell(String.valueOf(s.getSalesTodayQty()));

        table.addCell("Active Discounts");
        table.addCell(String.valueOf(s.getActiveDiscounts()));

        document.add(table);
        document.close();

        byte[] fileBytes = out.toByteArray();

        // 3) save report log in DB
        String fileName = "dashboard-summary-" + now.toLocalDate() + ".pdf";

        ReportLog saved = reportLogRepository.save(
                new ReportLog("DASHBOARD_SUMMARY_PDF", fileName, now)
        );

        // 4) return everything
        return new GeneratedReport(
                saved.getId(),
                saved.getReportType(),
                saved.getFileName(),
                saved.getGeneratedAt(),
                fileBytes
        );
    }

    // OPTIONAL: keep CSV generation too
    public GeneratedReport generateDashboardSummaryCsv() {

        DashboardSummaryResponse s = dashboardService.getSummary();

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

        LocalDateTime now = LocalDateTime.now();
        String fileName = "dashboard-summary-" + now.toLocalDate() + ".csv";

        ReportLog saved = reportLogRepository.save(
                new ReportLog("DASHBOARD_SUMMARY_CSV", fileName, now)
        );

        return new GeneratedReport(
                saved.getId(),
                saved.getReportType(),
                saved.getFileName(),
                saved.getGeneratedAt(),
                fileBytes
        );
    }
}