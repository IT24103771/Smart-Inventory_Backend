package com.example.demo.Sales;

import com.example.demo.Inventory.Inventory;
import com.example.demo.Inventory.InventoryRepository;
import com.example.demo.Products.Product;
import com.example.demo.Products.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final com.example.demo.Discount.DiscountRepository discountRepository;

    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       InventoryRepository inventoryRepository,
                       com.example.demo.Discount.DiscountRepository discountRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.discountRepository = discountRepository;
    }

    @Transactional
    public SaleResponse create(CreateSaleRequest req) {

        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new RuntimeException("Sale quantity must be at least 1.");
        }

        if (req.getSaleDate() == null) {
            throw new RuntimeException("Sale date is required.");
        }

        if (req.getSaleDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Sale date cannot be in the future.");
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory batch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        if (!batch.getProduct().getProductId().equals(product.getProductId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        LocalDate today = LocalDate.now();
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(today)) {
            throw new RuntimeException("Cannot sell from an expired batch.");
        }

        int qty = req.getQuantity();
        if (batch.getQuantity() < qty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + batch.getQuantity() + ", Requested: " + qty);
        }

        batch.setQuantity(batch.getQuantity() - qty);
        inventoryRepository.save(batch);

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setInventoryBatch(batch);
        sale.setQuantity(qty);
        sale.setSaleDate(req.getSaleDate());

        // Price & Discount Calculation
        Double originalPrice = product.getSellingPrice();
        if (originalPrice == null) originalPrice = 0.0;
        
        java.util.Optional<com.example.demo.Discount.Discount> activeDiscount = discountRepository.findActiveByProductIdAndBatchId(product.getProductId(), batch.getId());
        
        Double discountPct = 0.0;
        if (activeDiscount.isPresent()) {
            discountPct = activeDiscount.get().getDiscountPercent();
        }
        
        Double discountedPrice = originalPrice * (1.0 - (discountPct / 100.0));
        Double totalAmt = discountedPrice * qty;
        
        sale.setOriginalUnitPrice(originalPrice);
        sale.setDiscountPercent(discountPct);
        sale.setDiscountedUnitPrice(discountedPrice);
        sale.setTotalAmount(totalAmt);

        Sale saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    public List<SaleResponse> getAll() {
        return saleRepository.findAllWithProductOrderByDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SaleResponse update(Long id, CreateSaleRequest req) {

        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new RuntimeException("Sale quantity must be at least 1.");
        }

        if (req.getSaleDate() == null) {
            throw new RuntimeException("Sale date is required.");
        }

        if (req.getSaleDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Sale date cannot be in the future.");
        }

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Product newProduct = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory newBatch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        if (!newBatch.getProduct().getProductId().equals(newProduct.getProductId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        Inventory oldBatch = sale.getInventoryBatch();
        oldBatch.setQuantity(oldBatch.getQuantity() + sale.getQuantity());
        inventoryRepository.save(oldBatch);

        int newQty = req.getQuantity();
        if (newBatch.getQuantity() < newQty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + newBatch.getQuantity() + ", Requested: " + newQty);
        }

        newBatch.setQuantity(newBatch.getQuantity() - newQty);
        inventoryRepository.save(newBatch);

        sale.setProduct(newProduct);
        sale.setInventoryBatch(newBatch);
        sale.setQuantity(newQty);
        sale.setSaleDate(req.getSaleDate());

        // Price & Discount Calculation
        Double originalPrice = newProduct.getSellingPrice();
        if (originalPrice == null) originalPrice = 0.0;
        
        java.util.Optional<com.example.demo.Discount.Discount> activeDiscount = discountRepository.findActiveByProductIdAndBatchId(newProduct.getProductId(), newBatch.getId());
        
        Double discountPct = 0.0;
        if (activeDiscount.isPresent()) {
            discountPct = activeDiscount.get().getDiscountPercent();
        }
        
        Double discountedPrice = originalPrice * (1.0 - (discountPct / 100.0));
        Double totalAmt = discountedPrice * newQty;
        
        sale.setOriginalUnitPrice(originalPrice);
        sale.setDiscountPercent(discountPct);
        sale.setDiscountedUnitPrice(discountedPrice);
        sale.setTotalAmount(totalAmt);

        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public void delete(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Inventory batch = sale.getInventoryBatch();
        batch.setQuantity(batch.getQuantity() + sale.getQuantity());
        inventoryRepository.save(batch);

        saleRepository.deleteById(id);
    }

    private SaleResponse toResponse(Sale s) {
        
        String dNote = null;
        if (s.getDiscountPercent() != null && s.getDiscountPercent() > 0) {
            java.util.Optional<com.example.demo.Discount.Discount> d = discountRepository.findActiveByProductIdAndBatchId(s.getProduct().getProductId(), s.getInventoryBatch().getId());
            if (d.isPresent()) dNote = d.get().getNote();
        }

        return new SaleResponse(
                s.getId(),
                s.getProduct().getProductId(),
                s.getProduct().getProductName(),
                s.getInventoryBatch().getId(),
                s.getInventoryBatch().getBatchNumber(),
                s.getInventoryBatch().getExpiryDate(),
                s.getQuantity(),
                s.getOriginalUnitPrice(),
                s.getDiscountPercent(),
                s.getDiscountedUnitPrice(),
                s.getTotalAmount(),
                dNote,
                s.getSaleDate(),
                s.getCreatedAt()
        );
    }
}