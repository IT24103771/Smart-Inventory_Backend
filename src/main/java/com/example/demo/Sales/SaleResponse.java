package com.example.demo.Sales;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SaleResponse {
    private Long id;
    private Long productId;
    private String productName;

    // ✅ NEW
    private Long batchId;
    private String batchNumber;
    private LocalDate expiryDate;

    private Integer quantity;
    
    private Double originalUnitPrice;
    private Double discountPercent;
    private Double discountedUnitPrice;
    private Double totalAmount;
    private String discountNote;

    private LocalDate saleDate;
    private LocalDateTime createdAt;

    public SaleResponse(Long id,
                        Long productId,
                        String productName,
                        Long batchId,
                        String batchNumber,
                        LocalDate expiryDate,
                        Integer quantity,
                        Double originalUnitPrice,
                        Double discountPercent,
                        Double discountedUnitPrice,
                        Double totalAmount,
                        String discountNote,
                        LocalDate saleDate,
                        LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.originalUnitPrice = originalUnitPrice;
        this.discountPercent = discountPercent;
        this.discountedUnitPrice = discountedUnitPrice;
        this.totalAmount = totalAmount;
        this.discountNote = discountNote;
        this.saleDate = saleDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }

    public Long getBatchId() { return batchId; }
    public String getBatchNumber() { return batchNumber; }
    public LocalDate getExpiryDate() { return expiryDate; }

    public Integer getQuantity() { return quantity; }
    public Double getOriginalUnitPrice() { return originalUnitPrice; }
    public Double getDiscountPercent() { return discountPercent; }
    public Double getDiscountedUnitPrice() { return discountedUnitPrice; }
    public Double getTotalAmount() { return totalAmount; }
    public String getDiscountNote() { return discountNote; }

    public LocalDate getSaleDate() { return saleDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
