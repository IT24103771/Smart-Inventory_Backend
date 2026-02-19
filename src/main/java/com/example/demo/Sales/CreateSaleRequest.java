package com.example.demo.Sales;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateSaleRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Long batchId;   // ✅ NEW: which batch is being sold

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private LocalDate saleDate;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
}
