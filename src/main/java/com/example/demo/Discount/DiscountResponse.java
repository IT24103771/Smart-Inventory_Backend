package com.example.demo.Discount;


import java.time.LocalDateTime;

public class DiscountResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Double discountPercent;
    private String note;
    private Boolean active;
    private LocalDateTime createdAt;

    public DiscountResponse() {}

    public DiscountResponse(Long id, Long productId, String productName,
                            Double discountPercent, String note,
                            Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.discountPercent = discountPercent;
        this.note = note;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getDiscountPercent() { return discountPercent; }
    public String getNote() { return note; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }
    public void setNote(String note) { this.note = note; }
    public void setActive(Boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
