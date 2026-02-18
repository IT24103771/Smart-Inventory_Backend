package com.example.demo.Inventory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String batchNumber;
    private Integer quantity;
    private LocalDate expiryDate;
    private String status; // Safe / Expiring Soon / Expired
    private LocalDateTime createdAt;

    public InventoryResponse() {}

    public InventoryResponse(Long id, Long productId, String productName,
                             String batchNumber, Integer quantity,
                             LocalDate expiryDate, String status,
                             LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getBatchNumber() { return batchNumber; }
    public Integer getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
