package com.example.demo.Sales;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SaleResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private LocalDate saleDate;
    private LocalDateTime createdAt;

    public SaleResponse(Long id, Long productId, String productName,
                        Integer quantity, LocalDate saleDate, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public LocalDate getSaleDate() { return saleDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
